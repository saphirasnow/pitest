/*
 * Copyright 2011 Henry Coles
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations under the License. 
 */
package org.pitest.mutationtest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pitest.classinfo.CodeSource;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.coverage.CoverageGenerator;
import org.pitest.extension.TestListener;
import org.pitest.help.Help;
import org.pitest.help.PitHelpError;
import org.pitest.mutationtest.report.SourceLocator;
import org.pitest.mutationtest.verify.DefaultBuildVerifier;

public class MutationCoverageReportTest {

  private MutationCoverageReport testee;

  private ReportOptions          data;

  @Mock
  private ListenerFactory        listenerFactory;

  @Mock
  private TestListener           listener;

  @Mock
  private CoverageDatabase       coverageDb;
  
  @Mock
  private CoverageGenerator coverage;
  
  @Mock
  private CodeSource code;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.data = new ReportOptions();
    this.data.setSourceDirs(Collections.<File> emptyList());
    this.data.setMutators(Mutator.DEFAULTS.asCollection());
    when(coverage.calculateCoverage()).thenReturn(coverageDb);
   // when(this.coverageDb.initialise()).thenReturn(true);
    when(
        this.listenerFactory.getListener(any(CoverageDatabase.class),
            anyLong(), any(SourceLocator.class))).thenReturn(this.listener);

  }

  private void createAndRunTestee() {
    this.testee = new MutationCoverageReport(code,this.coverage, this.data,
        this.listenerFactory, new Timings(), new DefaultBuildVerifier());
    this.testee.run();
  }

  @Test
  public void shouldReportErrorWhenNoMutationsFoundAndFlagSet() {
    try {
      this.data.setFailWhenNoMutations(true);
      createAndRunTestee();
    } catch (final PitHelpError phe) {
      assertEquals(Help.NO_MUTATIONS_FOUND.toString(), phe.getMessage());
    }
  }

  @Test
  public void shouldReportErrorWhenNoMutationsFoundAndFlagNotSet() {
    this.data.setFailWhenNoMutations(false);
    createAndRunTestee();
  }
}
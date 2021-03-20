/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysds.test.functions.pipelines;

import org.apache.sysds.common.Types;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestConfiguration;
import org.apache.sysds.test.TestUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class CleaningTest extends AutomatedTestBase {
	private final static String TEST_NAME1 = "mainScript";
	private final static String TEST_NAME2 = "compareAccuracy";

	protected static final String SCRIPT_DIR = "./scripts/pipelines/";
	private final static String TEST_CLASS_DIR = SCRIPT_DIR + CleaningTest.class.getSimpleName() + "/";


	protected static final String DATA_DIR = SCRIPT_DIR+"/data/";

	private final static String DIRTY = DATA_DIR+ "dirty.csv";
	private final static String CLEAN = DATA_DIR+ "clean.csv";
	private final static String META = SCRIPT_DIR+ "meta/meta_census.csv";
	private final static String OUTPUT = SCRIPT_DIR+ "intermediates/";

	protected static final String PARAM_DIR = SCRIPT_DIR + "/properties/";
	private final static String PARAM = PARAM_DIR + "param.csv";
	private final static String PRIMITIVES = PARAM_DIR + "primitives.csv";

	@Override
	public void setUp() {
		addTestConfiguration(TEST_NAME1,new TestConfiguration(TEST_CLASS_DIR, TEST_NAME1,new String[]{"R"}));
		addTestConfiguration(TEST_NAME2,new TestConfiguration(TEST_CLASS_DIR, TEST_NAME2,new String[]{"R"}));
	}


	@Ignore
	public void testCP1() {
		runFindPipelineTest(1.0, 5,10, 2,
			true, Types.ExecMode.SINGLE_NODE);
	}

	@Test
	public void testCP2() {
		runCleanAndCompareTest( Types.ExecMode.SINGLE_NODE);
	}


	private void runFindPipelineTest(Double sample, int topk, int resources, int crossfold,
		boolean weightedAccuracy, Types.ExecMode et) {

		String HOME = SCRIPT_DIR+"scripts/" ;
		Types.ExecMode modeOld = setExecMode(et);
		try {
			loadTestConfiguration(getTestConfiguration(TEST_NAME1));
			fullDMLScriptName = HOME + TEST_NAME1 + ".dml";

			programArgs = new String[] {"-stats", "-exec", "singlenode", "-args", DIRTY, META, PRIMITIVES,
				PARAM, String.valueOf(sample), String.valueOf(topk), String.valueOf(resources),
				String.valueOf(crossfold), String.valueOf(weightedAccuracy), output("O"), OUTPUT };

			runTest(true, EXCEPTION_NOT_EXPECTED, null, -1);

			//expected loss smaller than default invocation
			Assert.assertTrue(TestUtils.readDMLBoolean(output("O")));
		}
		finally {
			resetExecMode(modeOld);
		}
	}

	private void runCleanAndCompareTest( Types.ExecMode et) {

		String HOME = SCRIPT_DIR+"scripts/" ;
		Types.ExecMode modeOld = setExecMode(et);
		try {
			loadTestConfiguration(getTestConfiguration(TEST_NAME2));
			fullDMLScriptName = HOME + TEST_NAME2 + ".dml";

			programArgs = new String[] {"-stats", "-exec", "singlenode", "-args", DIRTY, CLEAN, META, OUTPUT, output("O")};

			runTest(true, EXCEPTION_NOT_EXPECTED, null, -1);

			//expected loss smaller than default invocation
			Assert.assertTrue(TestUtils.readDMLBoolean(output("O")));
		}
		finally {
			resetExecMode(modeOld);
		}
	}

}

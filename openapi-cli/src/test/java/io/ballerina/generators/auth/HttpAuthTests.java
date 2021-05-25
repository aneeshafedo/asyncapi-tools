/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.generators.auth;

import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.VariableDeclarationNode;
import io.ballerina.generators.TestConstants;
import io.ballerina.openapi.cmd.Filter;
import io.ballerina.openapi.exception.BallerinaOpenApiException;
import io.swagger.v3.oas.models.OpenAPI;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.ballerina.generators.GeneratorUtils.getBallerinaOpenApiType;

/**
 * All the tests related to the auth related code snippet generation for http or oauth 2.0 mechanisms.
 */
public class HttpAuthTests {
    private static final Path RES_DIR = Paths.get("src/test/resources/generators/client/auth").toAbsolutePath();
    List<String> list1 = new ArrayList<>();
    List<String> list2 = new ArrayList<>();
    Filter filter = new Filter(list1, list2);


    @Test(description = "Generate config record for http basic auth", dataProvider = "httpAuthIOProvider")
    public void testgetConfigRecord(String yamlFile, String configRecord) throws IOException,
            BallerinaOpenApiException {
        Path definitionPath = RES_DIR.resolve("scenarios/http/" + yamlFile);
        OpenAPI openAPI = getBallerinaOpenApiType(definitionPath);
        String expectedConfigRecord = configRecord;
        String generatedConfigRecord = Objects.requireNonNull(
                BallerinaAuthConfigGenerator.getConfigRecord(openAPI)).toString();
        generatedConfigRecord = (generatedConfigRecord.trim()).replaceAll("\\s+", "");
        expectedConfigRecord = (expectedConfigRecord.trim()).replaceAll("\\s+", "");
        Assert.assertEquals(expectedConfigRecord, generatedConfigRecord);
    }

    @Test(description = "Test the generation of Config params in class init function signature",
            dependsOnMethods = {"testgetConfigRecord"})
    public void testgetConfigParamForClassInit() {
        String expectedParams = TestConstants.HTTP_CLIENT_CONFIG_PARAM;
        StringBuilder generatedParams = new StringBuilder();
        List<Node> generatedInitParamNodes = BallerinaAuthConfigGenerator.getConfigParamForClassInit();
        for (Node param: generatedInitParamNodes) {
            generatedParams.append(param.toString());
        }
        expectedParams = (expectedParams.trim()).replaceAll("\\s+", "");
        String generatedParamsStr = (generatedParams.toString().trim()).replaceAll("\\s+", "");
        Assert.assertEquals(expectedParams, generatedParamsStr);
    }

    @Test(description = "Test the generation of SSL init node",
            dependsOnMethods = {"testgetConfigRecord"})
    public void testgetSecureSocketInitNode() {
        String expectedParam = TestConstants.SSL_ASSIGNMENT;
        VariableDeclarationNode generatedInitParamNode = BallerinaAuthConfigGenerator.getSecureSocketInitNode();
        expectedParam = (expectedParam.trim()).replaceAll("\\s+", "");
        String generatedParamsStr = (generatedInitParamNode.toString().trim()).replaceAll("\\s+", "");
        Assert.assertEquals(expectedParam, generatedParamsStr);
    }

    @Test(description = "Test the generation of http:Client init node",
            dependsOnMethods = {"testgetConfigRecord"})
    public void testgetClientInitializationNode() {
        String expectedParam = TestConstants.HTTP_CLIENT_DECLARATION;
        VariableDeclarationNode generatedInitParamNode = BallerinaAuthConfigGenerator.getClientInitializationNode();
        expectedParam = (expectedParam.trim()).replaceAll("\\s+", "");
        String generatedParamsStr = (generatedInitParamNode.toString().trim()).replaceAll("\\s+", "");
        Assert.assertEquals(expectedParam, generatedParamsStr);
    }


    @DataProvider(name = "httpAuthIOProvider")
    public Object[][] dataProvider() {
        return new Object[][]{
                {"basic_auth.yaml", TestConstants.HTTP_BASIC_AUTH_CONFIG_REC},
                {"bearer_auth.yaml", TestConstants.HTTP_BEARER_AUTH_CONFIG_REC},
                {"multiple_auth.yaml", TestConstants.HTTP_MULTI_AUTH_CONFIG_REC}
        };
    }
}

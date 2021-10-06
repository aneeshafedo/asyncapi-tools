/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.openapi.generators.openapi;

import io.ballerina.openapi.converter.OpenApiConverterException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This test class for the covering the unit tests for record scenarios.
 */
public class RecordTests {
    private static final Path RES_DIR =
            Paths.get("src/test/resources/ballerina-to-openapi").toAbsolutePath();
    private Path tempDir;

    @BeforeMethod
    public void setup() throws IOException {
        this.tempDir = Files.createTempDirectory("bal-to-openapi-test-out-" + System.nanoTime());
    }
    @Test(description = "When the record field has typeInclusion it map to allOfSchema in OAS")
    public void testTypeInclusion() throws OpenApiConverterException, IOException {
        Path ballerinaFilePath = RES_DIR.resolve("record/typeInclusion.bal");
        //Compare generated yaml file with expected yaml content
        TestUtils.compareWithGeneratedFile(ballerinaFilePath, "record/typeInclusion.yaml");
    }
    @Test(description = "When the record field has optional fields it map to optional fields in OAS")
    public void testRequiredField() throws OpenApiConverterException, IOException {
        Path ballerinaFilePath = RES_DIR.resolve("record/optional.bal");
        TestUtils.compareWithGeneratedFile(ballerinaFilePath, "record/optional.yaml");
    }

    @Test(description = "When the record field has nullable fields it enables nullable true in OAS")
    public void testNullableFieldWithOptional() throws OpenApiConverterException, IOException {
        Path ballerinaFilePath = RES_DIR.resolve("record/nullable01.bal");
        TestUtils.compareWithGeneratedFile(ballerinaFilePath, "record/nullable01.yaml");
    }

    @Test(description = "When the record field has nullable fields which is required")
    public void testNullableFieldWithRequired() throws OpenApiConverterException, IOException {
        Path ballerinaFilePath = RES_DIR.resolve("record/nullable02.bal");
        TestUtils.compareWithGeneratedFile(ballerinaFilePath, "record/nullable02.yaml");
    }

    @Test(description = "When the record field has nullable field , which has record type reference data type")
    public void testNullableFieldWithTypeReference() throws OpenApiConverterException, IOException {
        Path ballerinaFilePath = RES_DIR.resolve("record/nullable03.bal");
        TestUtils.compareWithGeneratedFile(ballerinaFilePath, "record/nullable03.yaml");
    }

    @Test(description = "When the record field has nullable field , which has record type reference data type")
    public void testUnionTypeWithRecordType() throws OpenApiConverterException, IOException {
        Path ballerinaFilePath = RES_DIR.resolve("record/union.bal");
        TestUtils.compareWithGeneratedFile(ballerinaFilePath, "record/union.yaml");
    }

    @Test(description = "When the record field has nullable field , which has record type reference data type")
    public void testUnionTypeWithPrimitive() throws OpenApiConverterException, IOException {
        Path ballerinaFilePath = RES_DIR.resolve("record/union_with_primitive.bal");
        TestUtils.compareWithGeneratedFile(ballerinaFilePath, "record/union_with_primitive.yaml");
    }

    @Test(description = "When the record field has nullable field , which has record type reference data type",
            enabled = false)
    public void testUnionTypeWithNullable() throws OpenApiConverterException, IOException {
        Path ballerinaFilePath = RES_DIR.resolve("record/union_with_nullable.bal");
        TestUtils.compareWithGeneratedFile(ballerinaFilePath, "record/union.yaml");
    }

    @AfterMethod
    public void cleanUp() {
        TestUtils.deleteDirectory(this.tempDir);
    }

    @AfterTest
    public void clean() {
        System.setErr(null);
        System.setOut(null);
    }
}
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

package io.ballerina.asyncapi.codegenerator.controller;

import io.ballerina.asyncapi.codegenerator.configuration.BallerinaAsyncApiException;
import io.ballerina.asyncapi.codegenerator.configuration.Constants;
import io.ballerina.asyncapi.codegenerator.entity.Schema;
import io.ballerina.asyncapi.codegenerator.usecase.GenerateRecordNode;
import io.ballerina.asyncapi.codegenerator.usecase.GenerateUnionDescriptorNode;
import io.ballerina.asyncapi.codegenerator.usecase.GenerateUseCase;
import io.ballerina.compiler.syntax.tree.*;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createIdentifierToken;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createSimpleNameReferenceNode;

public class SchemaController implements BalController {
    private final Map<String, Schema> schemas;

    public SchemaController(Map<String, Schema> schemas) {
        this.schemas = schemas;
    }

    @Override
    public String generateBalCode(String balTemplate) throws BallerinaAsyncApiException {

        List<ModuleMemberDeclarationNode> recordNodes = new ArrayList<>();
        List<TypeDescriptorNode> typeDescriptorNodes = new ArrayList<>();
        for (Map.Entry<String, Schema> fields : schemas.entrySet()) {
            GenerateUseCase generateRecordNode = new GenerateRecordNode(schemas, fields);
            TypeDefinitionNode typeDefinitionNode = generateRecordNode.generate();
            if (typeDefinitionNode != null) {
                typeDescriptorNodes.add(
                        createSimpleNameReferenceNode(createIdentifierToken(typeDefinitionNode.typeName().text())));
                recordNodes.add(typeDefinitionNode);
            }
        }

        GenerateUseCase generateUnionNode = new GenerateUnionDescriptorNode(typeDescriptorNodes, Constants.GENERIC_DATA_TYPE);
        recordNodes.add(generateUnionNode.generate());

        TextDocument textDocument = TextDocuments.from(balTemplate);
        SyntaxTree syntaxTree = SyntaxTree.from(textDocument);
        ModulePartNode oldRoot = syntaxTree.rootNode();
        ModulePartNode newRoot = oldRoot.modify().withMembers(oldRoot.members().addAll(recordNodes)).apply();
        SyntaxTree modifiedTree = syntaxTree.replaceNode(oldRoot, newRoot);

        try {
            return Formatter.format(modifiedTree).toSourceCode();
        } catch (FormatterException e) {
            throw new BallerinaAsyncApiException("Could not format the generated code, " +
                    "may be a syntax issue in the generated code", e);
        }
    }
}
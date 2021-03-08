import ballerina/http;
import ballerina/openapi;

listener http:Listener ep0 = new (80, config = {host: petstore.openapi.io});

service /v1 on ep0 {
    resource function get pets(http:Request request, array[]? tags, int 'limit) returns Pet[]|Error {
    }
}

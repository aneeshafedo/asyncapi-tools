import ballerina/http;

listener http:Listener helloEp = new (9090);

@http:ServiceConfig {
    treatNilableAsOptional: false
}
service /payloadV on helloEp {
    resource function get ping(@http:Header string? headerValue) returns http:Ok {
        http:Ok ok = {body: ()};
        return ok;
    }
}
client "https://raw.githubusercontent.com/ballerina-platform/openapi-connectors/main/openapi/openweathermap/openapi.yaml" as foo;

public function main() {
    client "https://raw.githubusercontent.com/ballerina-platform/openapi-connectors/main/openapi/openweathermap/openapi.yaml" as bar;
    bar:client y;
    foo:client x;
}
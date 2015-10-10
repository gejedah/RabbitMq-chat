# RabbitMq-chat

## Requirements
 - JRE >= 1.7

## How to Build
1. Resolve maven dependency

	 ```
	 $ mvn dependency:copy-dependencies
	 ```
2. Build `jar` using maven `mvn`

	 ```
	 $ mvn package
	 ```

## How to Run
1. Run `ChatServer` from the generated `jar` in `target` folder

	 ```
	 $ java -cp target/dependency/*:target/rabbitmq-chat-1.0-SNAPSHOT.jar RPCServer
	 ```
2. Run `ChatClient` from the generated `jar` in `target` folder

	 ```
	 $ java -cp target/dependency/*:target/rabbitmq-chat-1.0-SNAPSHOT.jar RPCClient
	 ```

## Testing

## Team Member
- Edmund Ophie 13512095
- Kevin 13512097

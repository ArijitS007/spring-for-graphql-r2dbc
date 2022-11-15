# spring-for-graphql-r2dbc
Spring for GraphQL with R2DBC


Full course:
https://www.youtube.com/playlist?list=PLiwhu8iLxKwL1TU0RMM6z7TtkyW-3-5Wi

https://www.youtube.com/watch?v=rH2kdMPUQpQ&list=PLiwhu8iLxKwL1TU0RMM6z7TtkyW-3-5Wi&index=2

Spqr: https://www.youtube.com/watch?v=gnmKnu0nOdM&list=PLxZ6CHRlzYBWgAognsBMsIJx8MOp-jMMr&index=5

Project :
Using the dependency : spring-boot-starter-graphql which wraps spring-graphql
which is created on top of graphql-java

Client:L


# mutation {
#   addEmployee(addEmployeeInput:{name: "Rakesh", salary: "50000", departmentId: 1}){
#     id, name, salary
#   }	
# }

# query {
#   employeeByName(employeeName: "Praful") {
#     id, name, salary, departmentId
#   }
# }

# mutation {
#   updateSalary(updateSalaryInput :{
#     employeeId: "1",
#     salary: "90000"
#   }) {
#     id, name, salary, departmentId
#   }
# }

query {
  allDepartment{
    id, name, employees{
      id, name, salary, departmentId
    }
  }
}



GraphQL :
It’s a query language for API’s and provides a runtime for fulfilling the queries with the existing data. 
It provides a complete understandable description of the data in your API, 
Gives clients the power to ask for exactly what they need and nothing more.
Make it easier to evolve APIs over time 

GraphQL/Rest:
Client controls the response
Single endpoint which different query and variables
No over/under fetching of the endpoint
Decoupled business logic
Arguments passed in the root
Support for alias
Support for directive(include, skip)

Schema: (comes from the specification , defined by FB/open source)
Type -
 scaler (ID, String, Float, Boolean), [] - represents a collection / array
 Complex Object - creating a composite/complex object using scaler and/or complex datatype
 Fragment - to remove repeating json objects
Query
Mutation
Subscription - response goes on real time as there some change in the entity
Enum
Input
**Date - is represented by string itself

Runtime 
Validation
Execution 
Introspection 

Approaches
Schema First Approach - First schema is defined all request/communication has to comply with the schema
Complex, error prone since hand written and also has a learning curve
Code First Approach - The schema is defined in the runtime, only need to define the beans	

Dependencies :
GraphQL Spring boot which depends heavily on graphql-java


Setup:
Define ‘graphql.tools.schema-location-pattern= **/*.graphqls
Loads all the graphql schemas from the class path
Standard location = under the resources folder

We need to define the schema for graphql to map with the incoming request and also define the beans 
Internally for java to map with the defined schema
Break the schemas into complextype if required for  better design

Define all the entities with “type” with .graphqls extension 

Then create resolvers - implement GraphQLQueryResolver
If querying from the top level then use a GraphQLQueryResolver  and if nested then implement QueryResolver.

 
Use the property max.QueryDepth = to prevent recursion issue when an objects referring to its self which can result to into an DOS as a result of out of memory error if the if its recursively requested many times

Add “voyager” dependency to show diagrammatic schema dependency.
Can be enabled/disabled

Playground can be configured to load predefined queries and can be enabled/disabled
Also authorisation and authentication can be configured.
Exception-handlers-enabled = true

Exception:
GraphQLException which is a Runtime exception hiding the internal exception details
exception-handlers-enabled = true
Then we can define exception handlers like to we define in spring
The GraphQLException will also handle all RuntimeException by default so
Always define a RuntimeException handler as well
All custom define  error handlers are defined by DefaultGraphQLErrorHandler which implements the GraphQLErrorHandler
We can implement the GraphQLErrorHandler to create our CustomGraphQLHandler and override the processError method.

DataFetcherResult is to send partial data/response with custom errors embedded for the object property that could not be fetched. Specially can be used when there are two different entities fetched from different services.
This is a generic which can be used with the higher level entity.

Concurrent/Asynchronous
As a default behaviour , all resolvers are synchronous in nature and will wait before the previous resolvers completes as all shares the same tomcat thread even though they are not dependent
What we can do is to return CompletableFuture which would be run on a separate ThreadPool instead
of the default thread(http-nio) and this way they can executed asynchronously as well as concurrantly

FileUpload
Use the DataFetchingEnviroment to get servletContext to get the file parts

DataFetchingEnviroment
Last parameter GraphQL framework will automatically inject the same
Imp Fields
SelectionSet - set of fields requested by the user
Context
Variables
Arguments
Dataloader
ExecutionID**

graphQL-java-extended-scalers: Introduce more scalers than already supported.
Exclude graphQL-java
Also used to create a custom scaler

Input Validation /Bean Validation (hibernate validator)
@Validated, @Valid

GraphQLServletListner - implement to listen to various servlet request lifecycle(onRequest, onSuccess,onError, OnFinally)

Pagination:
Plural - Simplest way to expose connection between 2 object is with a field that return all
Slicing - Ask the data with a condition like first 2
Paging - 
friends(first:2, offset:2) - give me the first 2 after the offset number or ignoring from the offset number

friends(first:2, after: $friendId) - give me the data after the respective id. This is also kind of a weak form as
It creates a hard coupling with the ordering.

friends(first:2, after: $friendCursor) -most powerful. Here we get the cursor from the last item and use that to paginate. If cursors are opaque both Id and offset based pagination can be implemented using cursors by
Making the cursor either the offset or the id. It also gives the flexibility if the pagination model changes in future.
Cursor being opaque the format of the cursor should be relied on, should be base64 encoded as a best practise.
This introduces a new concept of edges which holds both the cursor and the underlying node.
Cursor can be considered as a pointer to the previous edge (encoded: base64) by encoding something like the 
previous id

Finally to add more capabilities and flexibility we create a Connection object which contains a whole lot more info
TotalCount
Edge - node and cursor
PageInfo (startCursor, endCursor, hasNextPage, hasPreviousPage)

Read about pagination and connection : https://graphql.org/learn/pagination/

Offset vs Seek:
https://www.youtube.com/watch?v=QLdBJ4VBmC8
Seek is better than offset in terms of latency because it jumps	

 Spqr
IT follows an Code first approach and not explicit schema definition is required
Define the grail resolvers as spring component and also as a @GraphQLApi
Use GraphQLQuery for GraphQLMutation accordingly
GraphQLArgument for defining an argument
GraphQLContext - tells graphQL to use the same entity to fetch another a=entity
Automatically the dependent entity be made part of the main entity 
GraphQLInputField (InputType) - when the argument is not a scaler type but a complex type 
** always define name wether when using graphqlargument or graphqlinputfield

Subscription is long running HTTP connection similar to web socket which is listening to a perticular operation say

https://www.youtube.com/watch?v=IK26KdGRl48&list=PLnXn1AViWyL70R5GuXt_nIDZytYBnvBdd

Since we are putting an `else` condition, i hope writing back is mutually exclusive which means we only write to s4 when we are not writing to Syclo for the any of Syclo related conditions not satisfying.



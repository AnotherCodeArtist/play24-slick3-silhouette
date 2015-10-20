# Play 2.4.x Sample App with Slick and Silhouette integration

This application demonstrates how to integrate Play 2.4 with Slick and Silhoutte. 
It is mainly based on the [Play Silhouette Angular Seed](https://github.com/mohiva/play-silhouette-angular-seed) 
but also demonstrates how to write, configure and run test cases.

##Configuring/Setting up the Database

The project is setup to be used with a PostgreSQL database. Thus, before you can run (and test) it, 
you need to setup the required dabase. The configuration of the database can be found in
the [./conf/application.conf](./conf/application.conf) file.

```
slick.dbs.default.driver="slick.driver.PostgresDriver$"
slick.dbs.default.db.driver="org.postgresql.Driver"
slick.dbs.default.db.url="jdbc:postgresql://localhost/swengs"
slick.dbs.default.db.user="swengs"
slick.dbs.default.db.password="swengs"
```
So out of the box the name of the database, user and password is _swengs_.

After you have created the datebase user and the the database, you are good to load the project in activator (Simply run 'activator' from a command shell at your project root directory)

##Building the project

Since the project includes an Angular front-end (found in the ui-folder) you need to install all the 
necessary JavaScript tooling and JavaScript libraries. Therefore type `npm install`in your activator console.
This also runs _bower install_ as part of the post-install hook.

In order to build the JavaScript client (actually CoffeeScript is used to write it) type `grunt` into 
your activator console.

Now you are good to run it. Therefore type `run` in the activator console

#Running Tests

In order to run the spec-files simply run `test` from your activator shell

All tests that are needing a database connection are using an in-memory database. This test database is configured in a trait [helpers.SecurityTestContext](./test/helpers/SecurityTestContext.scala).

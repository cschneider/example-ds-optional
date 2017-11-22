# Example for working with optional packages in declarative services

When designing your bundles a typical problem is that some dependency might be optional.
So you can either split your code in two bundles or have one bundle with an optional Import-Package. To avoid having too many bundles the optional import often is the better choice.

## Problem

The problem now is how do you refer to an optional service if even the api package is sometimes not present. As an example we take the EventAdmin. A naive approach might look like this:

  @Component
  class MyComponent {
    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    EventAdmin eventAdmin;

    ...
    use eventAdmin
    ...
  }

Now the problem is that your class depends on the org.osgi.event package that might not be present. So your complete component can not be resolved. You can try to overcome this using Object and loading the class yourself but this code is ugly and error prone.

## Solution

So the better solution is to split up your code into two parts:

@Component
public class MyComponent {

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    EventSender sender;

    ...
    use sender and check for null as it might not be injected
    ...
}

@Component
public class EventAdminSender implements EventSender {
    @Reference
    EventAdmin eventAdmin;

    ...
    use eventAdmin
    ...
}

EventSender is an interface in your own bundle that decouples MyComponent from the package that is maybe not present.

If you start your bundle and org.osgi.event is not present then scr will simply not instantiate the EventAdminSender. It will not complain about the missing package and simply display the component as unsatisfied.

If you then install an eventadmin bundle and referesh and restart your bundle then scr will start EventAdminSender and wire it to MyComponent. So this solves the optional import problem in a very elegant way.

## Test in apache karaf

Download apache-karaf-minimal 4.1.3. You need minimal as it does not start eventadmin by default.

In karaf do:

    feature:install scr
    install -s mvn:net.lr.ds.optional/dsopt/1.0.0-SNAPSHOT
    scr:list
    message test

Scr list will show that MessageCommand is satisfied and EventAdminSender is unsatisfied.
The last command will display "test". So it will only print to stdout but not send to EventAdmin.

Now we install eventadmin and try again:

    feature:install eventAdmin
    scr:list
    message test

Now EventAdminSender will be active and wired to MessageCommand. The last command will print "test" and in a new line "Sent message to EventAdmin". So this shows we can work gracefully with and without EventAdmin without any classloader tricks.

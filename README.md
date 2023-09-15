# Code Coverage Cheating

## What does this package do?
It can be used to automatically call an object's getters, setters, toString, hashCode and equals.

## How do I use it?
```java
 new Coverage(new MyObject()).cover();
```

## Why would I want to do that?
To increase the unit test code coverage "percentage" with minimum effort, especially in projects that use a lot
of value objects, such as Entity classes, JSON representations etc.

## Isn't that completely pointless?
Yes, it will add no value to your test suite, and may give you a false sense of security.

The target use case, though, is in when developing software in large [enterprises](https://github.com/EnterpriseQualityCoding/FizzBuzzEnterpriseEdition).

>Manager: We need to make sure that every class has at least 75% code coverage.
>
>_You: Even our value objects? They are nothing but a bunch of getters and setters._
>
>Manager: Well, I can _try_ to get an exception, but I think the Big Bosses are rather fond of the idea.
>
>_You: Oh, I see. I'll start writing some useless tests, then._

This package helps you minimize the amount of time you spend writing useless tests.

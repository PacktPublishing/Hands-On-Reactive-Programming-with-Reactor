<!DOCTYPE html>
<html>
    <head>
        <title>Reactor Sample</title>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    </head>
    <body>
        <h1>Fibonacci Numbers</h1>
        <ul style="list-style-type:circle">
        <#list series as number>
          <li>${number}</li>
        </#list>
        </ul>
    </body>
</html>

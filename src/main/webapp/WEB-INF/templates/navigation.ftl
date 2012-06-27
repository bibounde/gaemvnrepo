<html>
    <head>
        <title>${caption}</title>
    </head>
    <body>
        <ul>
            <#list items as item>
            <li><a href="${item.link}">${item.name}</a></li>
            </#list> 
        </ul>
    </body>
</html>
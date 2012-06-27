<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>$$TOTO</title>
    <script type="text/javascript">
        var styles = window.parent.document.styleSheets;
        for(var j = 0; j < styles.length; j++) {
            if(styles[j].href) {
                var stylesheet = document.createElement("link");
                stylesheet.setAttribute('rel', 'stylesheet');
                stylesheet.setAttribute('type', 'text/css');
                stylesheet.setAttribute('href', styles[j].href);
                document.getElementsByTagName('head')[0].appendChild(stylesheet);
            }
        }
    </script>
</head>
<body onload="document.forms[0].j_username.focus();">
    <form action="/j_spring_security_check" method="post" target="_parent">
    <table width="100%" cellpadding="2px" class="v-app v-app-loginpage" style="background:transparent;">
    <tr>
        <td>${labellogin}</td>
        <td width="100%"><input style="width:100%;"  class="v-textfield" name="j_username"></td>
    </tr>
    <tr>
        <td>${labelpassword}</td>
        <td width="100%"><input style="width:100%;" class="v-textfield" type="password" name="j_password"/></td>
    </tr>
    <tr>
        <td></td>
        <td><input type="submit" value="${labelsubmit}"></td>
    </tr>
    </form>
</table>
</body>
</html>


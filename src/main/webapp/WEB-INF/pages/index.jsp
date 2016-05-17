<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <link rel="shortcut icon" href="http://vk.com/images/faviconnew.ico"/>
    <title>vk2rss</title>
</head>
<body>
<div>
    Чтобы получить RSS ленту публичной группы, перейдите по адресу /vk/{groupId}<br/>
    Например, если у группы адрес <a href="http://vk.com/mygroup">http://vk.com/mygroup</a>,
    её фид будет по адресу /vk/mygroup
</div>
<form method="post" action="feed">
    <label for="link">Ссылка на публичную группу:</label>
    <input name="link" id="link">
    <input type="submit" value="Получить фид">
</form>
</body>
</html>
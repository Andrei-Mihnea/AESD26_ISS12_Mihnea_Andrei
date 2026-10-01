<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Welcome</title>
</head>
<body>
    <h1>Welcome</h1>

    <form method="post"
          action="${pageContext.request.contextPath}/controller">

        <label for="page">Choose a page:</label>

        <select id="page" name="page">
            <option value="1">Page 1</option>
            <option value="2">Page 2</option>
        </select>

        <button type="submit">Submit</button>
    </form>
</body>
</html>

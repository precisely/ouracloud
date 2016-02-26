<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Admin Dashboard</title>
</head>
<body>
    <div class="container">
        <h1 class="page-header">
            Admin Dashboard
        </h1>

        <ul>
            <li><g:link uri="/client">Clients</g:link></li>
            <li><g:link action="summaryData">Summary Data</g:link></li>
            <li><g:link uri="/user/upload">User import</g:link></li>
        </ul>
    </div>
</body>
</html>
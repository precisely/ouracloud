<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'client.label', default: 'Client')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
</head>
<body>
    <div class="container">
        <h1 class="page-header">
            <g:message code="default.create.label" args="[entityName]" />
        </h1>

        <g:form action="save" class="form-horizontal">
            <fieldset>
                <g:render template="form" />
                <div class="form-group well">
                    <div class="col-lg-offset-2">
                        <g:submitButton name="create" class="btn btn-primary"
                            value="${message(code: 'default.button.create.label')}" />
                    </div>
                </div>
            </fieldset>
        </g:form>
    </div>
</body>
</html>
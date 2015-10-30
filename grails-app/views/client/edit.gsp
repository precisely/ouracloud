<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'client.label', default: 'Client')}" />
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
</head>
<body>
    <div class="container">
        <h1 class="page-header">
            <g:message code="default.edit.label" args="[entityName]" />
        </h1>

        <g:form class="form-horizontal" >
            <g:hiddenField name="id" value="${clientInstance.id}" />
            <g:hiddenField name="version" value="${clientInstance.version}" />

            <fieldset class="form">
                <g:render template="form" />

                <div class="well">
                    <div class="col-lg-offset-2">
                        <g:actionSubmit class="btn btn-primary" action="update"
                            value="${message(code: 'default.button.update.label')}" />
                        <g:actionSubmit class="btn btn-danger" action="delete"
                            value="${message(code: 'default.button.delete.label')}"
                            onclick="return confirm('${message(code: 'default.button.delete.confirm.message')}');" />
                    </div>
                </div>
            </fieldset>
        </g:form>
    </div>
</body>
</html>
<g:hasErrors bean="${clientInstance}">
	<ul class="text-danger fa-ul">
		<g:eachError bean="${clientInstance}" var="error">
			<li>
				<i class="fa-li fa fa-exclamation-triangle"></i><g:message error="${error}" />
			</li>
		</g:eachError>
	</ul>
</g:hasErrors>

<div class="form-group ${hasErrors(bean: clientInstance, field: 'name', 'has-error')}">
	<label class="control-label col-lg-2" for="clientId">
		Client Name
	</label>
	<div class="col-lg-4">
		<g:textField name="name" value="${clientInstance.name}" class="form-control" autofocus=""
			placeholder="Client Name like Curious Inc." />
	</div>
</div>

<div class="form-group ${hasErrors(bean: clientInstance, field: 'clientId', 'has-error')}">
	<label class="control-label col-lg-2" for="clientId">
		<g:message code="client.clientId.label" default="Client Id" />
	</label>
	<div class="col-lg-4">
		<g:textField name="clientId" value="${clientInstance.clientId}" class="form-control" autofocus=""
			placeholder="Client ID like curious" />
	</div>
</div>

<div class="form-group ${hasErrors(bean: clientInstance, field: 'clientSecret', 'has-error')}">
	<label class="control-label col-lg-2" for="clientSecret">
		<g:message code="client.clientSecret.label" default="Client Secret" />
	</label>
	<div class="col-lg-4">
		<g:textField name="clientSecret" value="${clientInstance.clientSecret}" disabled="" class="form-control" />
	</div>
</div>

<div class="form-group ${hasErrors(bean: clientInstance, field: 'accessTokenValiditySeconds', 'has-error')}">
	<label class="control-label col-lg-2" for="accessTokenValiditySeconds">
		<g:message code="client.accessTokenValiditySeconds.label" default="Access Token Validity Seconds" />
	</label>
	<div class="col-lg-4">
		<g:field type="number" name="accessTokenValiditySeconds" value="${clientInstance.accessTokenValiditySeconds}"
			class="form-control" min="0" />
	</div>
</div>

<div class="form-group ${hasErrors(bean: clientInstance, field: 'refreshTokenValiditySeconds', 'has-error')}">
	<label class="control-label col-lg-2" for="refreshTokenValiditySeconds">
		<g:message code="client.refreshTokenValiditySeconds.label" default="Refresh Token Validity Seconds" />
	</label>
	<div class="col-lg-4">
		<g:field type="number" name="refreshTokenValiditySeconds" value="${clientInstance.refreshTokenValiditySeconds}"
			class="form-control" min="0" />
	</div>
</div>

<div class="form-group ${hasErrors(bean: clientInstance, field: 'authorities', 'has-error')}">
	<label class="control-label col-lg-2" for="authorities">
		<g:message code="client.authorities.label" default="Authorities" />
	</label>
	<div class="col-lg-4">
		<g:select name="authorities" from="${us.wearecurio.users.Role.list()}" optionKey="authority" class="form-control"
			optionValue="authority" value="${clientInstance.authorities}" multiple="true"></g:select>
	</div>
</div>

<div class="form-group ${hasErrors(bean: clientInstance, field: 'authorizedGrantTypes', 'has-error')}">
	<label class="control-label col-lg-2" for="authorizedGrantTypes">
		<g:message code="client.authorizedGrantTypes.label" default="Authorized Grant Types" />
	</label>
	<div class="col-lg-4">
		<g:select name="authorizedGrantTypes" from="${['authorization_code', 'refresh_token', 'implicit', 'password', 'client_credentials']}"
			value="${clientInstance.authorizedGrantTypes}" multiple="true" class="form-control"></g:select>
	</div>
</div>

<div class="form-group ${hasErrors(bean: clientInstance, field: 'redirectUris', 'has-error')}">
	<label class="control-label col-lg-2" for="redirectUris">
		<g:message code="client.redirectUris.label" default="Redirect URIs" />
	</label>
	<div class="col-lg-4">
		<g:textField name="redirectUris" class="form-control" placeholder="Add comma separated values"
			value="${clientInstance.redirectUris?.join(', ')}" />
	</div>
</div>

<div class="form-group ${hasErrors(bean: clientInstance, field: 'resourceIds', 'has-error')}">
	<label class="control-label col-lg-2" for="resourceIds">
		<g:message code="client.resourceIds.label" default="Resource Ids" />
	</label>
	<div class="col-lg-4">
		<g:textField name="resourceIds" class="form-control" placeholder="Add comma separated values"
			value="${clientInstance.resourceIds?.join(', ')}" />
	</div>
</div>

<div class="form-group ${hasErrors(bean: clientInstance, field: 'clientHookURL', 'has-error')}">
	<label class="control-label col-lg-2" for="clientHookURL">
		<g:message code="client.scopes.label" default="Client hook URL" />
	</label>
	<div class="col-lg-4">
		<g:textField name="clientHookURL" value="${clientInstance.clientHookURL}" class="form-control" placeholder="Client hook URL" />
	</div>
</div>

<div class="form-group ${hasErrors(bean: clientInstance, field: 'scopes', 'has-error')}">
	<label class="control-label col-lg-2" for="scopes">
		<g:message code="client.scopes.label" default="Scopes" />
	</label>
	<div class="col-lg-4">
		<g:textField name="scopes" class="form-control" placeholder="Add comma separated values"
			value="${clientInstance.scopes?.join(', ')}" />
	</div>
</div>

<div class="form-group ${hasErrors(bean: clientInstance, field: 'autoApproveScopes', 'has-error')}">
	<label class="control-label col-lg-2" for="autoApproveScopes">
		<g:message code="client.autoApproveScopes.label" default="Auto Approve Scopes" />
	</label>
	<div class="col-lg-4">
		<g:textField name="autoApproveScopes" class="form-control" placeholder="Add comma separated values"
			value="${clientInstance.autoApproveScopes?.join(', ')}" />
	</div>
</div>

<div class="form-group ${hasErrors(bean: clientInstance, field: 'environment', 'has-error')}">
	<label class="control-label col-lg-2" for="environment">
		<g:message code="client.environment.label" default="Environment" />
	</label>
	<div class="col-lg-4">
		<select name="environment" id="environment" class="form-control">
			<g:each in="${us.wearecurio.oauth.ClientEnvironment.values()}" var="environment">
				<option value="${environment.name()}"
					${clientInstance.environment.name == environment.name ? "selected" : ""}>${environment.name}</option>
			</g:each>
		</select>
	</div>
</div>
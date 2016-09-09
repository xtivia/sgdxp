Service Guard for Liferay 7 DXP
-------------------------------

This is the source code repository for Xtivia's Service Guard for Liferay 7 DXP. ServiceGuard is framework that allows you to protect JAX-RS services using Liferay DXP's roles and organizations model via simple Java annotations.

As a brief example, if you had written and deployed a JAX-RS endpoint in DXP, and you  wanted to permit only users who were assigned to the portal role named "Marketing" to be able to execute AJAX calls to that endpoint, you would simply add an annotation like this

*@RegularRole("Marketing")*

to the code for the service. After the updated code is deployed then only users assigned to the *Marketing* role will be permitted to invoke the endpoint.

Service Guard works in both Liferay 7 CE and Liferay 7 DXP. It has been implemented using fully compliant JAX-RS based extensions, in particular JAX-RS Filters.

For complete reference documentation and usage details please refer to the Service Guard example application project [here](https://github.com/xtivia/sgdxp-example) on GitHub.
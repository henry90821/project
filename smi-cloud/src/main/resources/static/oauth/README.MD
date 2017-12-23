#星美资源认证服务说明文档
*本认证服务采用标准的[OAuth2.0](http://tools.ietf.org/html/draft-ietf-oauth-v2)协议规范设计，在访问所有受协议保护的资源时必须先按照该规范从本服务获取授权信息才能正常使用资源服务。*
>在使用认证服务时主要有以下几个步骤：
>
>1. 从认证服务器获取授权CODE
>2. 通过授权CODE获取ACCESS_TOKEN和REFRECH\_TOKEN
>3. 将ACCESS_TOKEN发送至资源服务器进行资源请求

####从认证服务器获取授权CODE
>获取授权CODE的地址是`http://{server_path}/oauth/authorize`，使用`GET`方式进行请求，资源认证服务将以异步响应的方式将授权`code`返回给请求方。
>
>请求参数：
>
>|参数名        |类型   |备注|
>|-------------|------|----|
>|Authorization|header|授权公钥标识，参数值必须以`Basic `开头再加上公钥值。例如：`Basic xxxxxxxx`**（注意`Basic `后面有一个空格不能省略）**|
>|client_id    |query |分配给客户端的ID，由服务端生成分配|
>|response_type|query |响应类型，使用固定值`code`|
>|redirect_uri |query |分配`code`时跳转的URL地址，资源认证服务器在请求校验通过后会将生成的`code`作为参数请求该地址返回给客户端，例如：`http://{client_path}/{receive_path}?code=xxxx`|

####通过授权CODE获取ACCESS_TOKEN和REFRESH\_TOKEN
>获取ACCESS_TOKEN的地址是`http://{server_path}/oauth/token`，使用`POST`方式进行请求，资源认证服务将同步返回`access_token`给请求方。刷新`access_token`的有效期使用的也是当前地址，只不过传递的参数不一样而已。
>
>请求参数：
>
>|参数名        |类型   |备注|
>|-------------|------|----|
>|Authorization|header|授权私钥标识，参数值必须以`Basic `开头再加上私钥值。例如：`Basic xxxxxxxx`**（注意`Basic `后面有一个空格不能省略）**|
>|grant_type   |query |授权类型，其值包含`authorization_code`(通过`code`获取`access_token`)和`refresh_token`(刷新有效期并获取新的`access_token`)|
>|code         |query |授权码，资源认证服务分配的授权码，在`grant_type`的值为`authorization_code`时通过分配的`code`值获取用来访问资源的`access_token`值，`code`在使用一次后即失效|
>|redirect_uri |query |需要与调用`/oauth/authorize`获取`code`的接口时传递的参数值保持一致，用以校验两次请求接口是否为同一个`client`端，在`grant_type`的值为`refresh_token`时不需要该参数|
>|refresh_token|query |刷新令牌，在`grant_type`的值为`refresh_token`时需要传递该参数告诉授权服务器需要刷新哪个`access_tokon`值|
>
>响应结构（以JSON格式返回数据结构）：
>
>     {"access_token":"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx","token_type":"bearer","refresh_token":"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx","expires_in":7199,"scope":"app"}
>
>结构说明：
>
>|字段名        |类型    |备注|
>|-------------|-------|----|
>|access_token |String |访问令牌，用来对资源服务进行授权范围，默认有效期为2小时|
>|token_type   |String |令牌类型，通常默认值为`bearer `|
>|refresh_token|String |刷新令牌，在请求刷新`access_token`时所需要的令牌值|
>|expires_in   |Integer|令牌有效期，标识访问令牌剩下的有效时间|
>|scope        |String |访问令牌有效域，标识当前令牌能够访问哪些作用域的资源|

####将ACCESS_TOKEN发送至资源服务器进行资源请求
>访问受保护的资源服务器时只需按照资源服务的约束进行接口请求，只需将认证服务提供的`access_token`放入请求的`HEADER`中进行授权认证即可。校验通过了后会直接返回所请求的资源内容，`http_code`为`200`，校验失败`http_code`则返回`401`，客户端通过`http_code`来进行判断是否授权成功。
>
>请求参数：
>
>|参数名        |类型   |备注|
>|-------------|------|----|
>|Authorization|header|授权令牌，参数值必须以`Bearer `开头再加上`access_token`。例如：`Bearer xxxxxxxx`**（注意`Basic `后面有一个空格不能省略）**|
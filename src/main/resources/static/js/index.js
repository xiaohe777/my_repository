layui.use(['form','jquery','jquery_cookie','layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    /**
     * 用户登录，表单提交
     */
    form.on("submit(login)",function (data){
        //获取表单元素的值
        var feildData = data.field;

        //判断参数是否为空
        if (feildData.username == "undefined" || feildData.username == ''){
            layer.msg("用户名不能为空");
            return false;
        }
        if (feildData.password == "undefined" || feildData.password == ''){
            layer.msg("密码不能为空");
            return false;
        }
        //发送ajax请求，请求登录
        $.ajax({
            type:"post",
            url:ctx + "/user/login",
            data:{
                userName:feildData.username,
                userPwd:feildData.password
            },
            dataType:"json",
            success:function (data){
                //判断是否登录成功
                if(data.code == 200){
                    layer.msg("登陆成功",function (){
                        //将信息存入cookie
                        var result = data.result;
                        $.cookie("userIdStr",result.userIdStr);
                        $.cookie("userName",result.userName);
                        $.cookie("trueName",result.trueName);
                        //若选择记住我，将cookie存活改为7天
                        if($("input[type = 'checkbox']").is(":checked")){
                            $.cookie("userIdStr",result.userIdStr,{expires:7});
                            $.cookie("userName",result.userName,{expires:7});
                            $.cookie("trueName",result.trueName,{expires:7});
                        }
                        //跳转
                        window.location.href = ctx + "/main";
                    });
                }else {
                    //提示信息
                    layer.msg(data.msg);
                }
            }
        });
        //阻止页面跳转
        return false;
    })
});
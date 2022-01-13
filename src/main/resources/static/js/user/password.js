layui.use(['form','jquery','jquery_cookie','layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    /**
     * 用户登录，表单提交
     */
    form.on("submit(saveBtn)",function (data){
        //获取表单元素的值
        var feildData = data.field;

        //发送ajax请求，请求登录
        $.ajax({
            type:"post",
            url:ctx + "/user/updatePwd",
            data:{
                oldPassword:feildData.old_password,
                newPassword:feildData.new_password,
                confirmPassword:feildData.again_password
            },
            dataType:"json",
            success:function (data){
                //判断是否成功
                if(data.code == 200){
                    layer.msg("修改密码成功",function (){
                        //推出系统后，删除对应cookie
                        var result = data.result;
                        $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
                        $.removeCookie("userName",{domain:"localhost",path:"/crm"});
                        $.removeCookie("trueName",{domain:"localhost",path:"/crm"});
                        //跳转
                        window.parent.location.href = ctx + "/index";
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
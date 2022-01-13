layui.use(['form', 'layer', 'formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    // 引入 formSelects 模块
    formSelects = layui.formSelects;


    //监听提交
    /**
     * 添加或更新操作
     */
    form.on('submit(addOrUpdateUser)', function (data) {
        // 提交数据时的加载层
        var index = layer.msg("数据提交中,请稍后...", {
            icon: 16, // 图标
            time: false, // 不关闭
            shade: 0.8 // 设置遮罩的透明度
        });
        var url = ctx + "/user/save";
        if ($("input[name='id']").val()) {
            //更新
            url = ctx + "/user/update";
        }
        // 发送ajax请求
        $.post(url, data.field, function (result) {
            // 操作成功
            if (result.code == 200) {
                // 提示成功
                layer.msg("操作成功！");
                // 关闭加载层
                layer.close(index);
                //关闭弹出层
                layer.closeAll("iframe");
                // 刷新父页面，重新渲染表格数据
                parent.location.reload();
            } else {
                layer.msg(result.msg);
            }
        });
        return false;
    });
    /**
     * 关闭弹出层
     */
    $(".layui-btn-normal").click(function () {
        //得到当前iframe层的索引
        var index = parent.layer.getFrameIndex(window.name);
        //执行关闭
        parent.layer.close(index);
    });
    /**
     * 加载下拉框数据
     */
    formSelects.config('selectId',{
        type:"post",
        searchUrl:ctx + "/role/queryAllRoles?userId=" + $("input[name=id]").val(),
        //自定义返回数据中name的key, 默认 name
        keyName: 'roleName',
        //自定义返回数据中value的key, 默认 value
        keyVal: 'id'
    },true);
});
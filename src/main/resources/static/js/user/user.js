layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 用户列表展示
     */
    var tableIns = table.render({
        elem: '#userList',
        url : ctx+'/user/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "userListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'userName', title: '用户名', minWidth:50, align:"center"},
            {field: 'email', title: '用户邮箱', minWidth:100, align:'center'},
            {field: 'phone', title: '用户电话', minWidth:100, align:'center'},
            {field: 'trueName', title: '真实姓名', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150,
                templet:'#userListBar',fixed:"right",align:"center"}
        ]]
    });

    /**
     * 绑定搜索按钮的点击事件
     */
    $(".search_btn").click(function () {
        tableIns.reload({
            where: {
                //设定参数
                userName: $("input[name='userName']").val(),//用户名
                email: $("input[name='email']").val(),//邮箱
                phone: $("input[name='phone']").val()//手机
            },
            page: {
                curr: 1,//重新加载从第一页开始
            }
        });
    });

    /**
     * 头部工具栏
     */
    table.on('toolbar(users)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        switch(obj.event){
            case 'add':
                openAddOrUpdateUserDialog();
                break;
            case 'del':
                deleteUser(checkStatus.data);//批量删除
                break;
        };
    });

    /**
     * 批量删除数据
     */
    function deleteUser(data){
        if (data.length == 0){
            layer.msg("请选择数据");
            return;
        }
        //询问用户是否删除
        layer.confirm("您确定要删除选中的数据吗", {btn: ["赶紧删", "不删了"],}, function (index) {
            //关闭确认框
            layer.close(index);
            var ids = [];
            for (var x in data) {
                ids.push(data[x].id);
            }
            //发送ajax请求
            $.ajax({
                type: "post",
                url: ctx + "/user/delete",
                data: {"ids": ids.toString()},
                dataType: "json",
                success: function (obj) {
                    if (obj.code == 200) {
                        layer.msg("删除成功", {icon: 1.5});
                        //加载表格
                        tableIns.reload();
                    } else {
                        layer.msg(obj.msg);
                    }
                },
            });
        });
    }

    /**
     * 修改还是添加
     * @param userId
     */
    function openAddOrUpdateUserDialog(userId){
        var url = ctx + "/user/addOrUpdateUserPage";
        var title = "添加用户";
        if(userId){
            url = url + "?id=" + userId;
            title = "修改用户信息";
        }
        layui.layer.open({
            title:title,
            type: 2,//iframe
            area:["650px","400px"],
            maxmin:true,
            content:url,
        });
    }
    /**
     * 行内工具栏
     */
    table.on('tool(users)', function(obj){
        var data = obj.data;
        if(obj.event === 'del'){
            //询问用户是否删除
            layer.confirm("您确定要删除这条数据吗", {btn: ["赶紧删", "不删了"],}, function (index) {
                //关闭确认框
                layer.close(index);
                //发送ajax请求
                $.ajax({
                    type: "post",
                    url: ctx + "/user/delete",
                    data: {"ids": data.id},
                    dataType: "json",
                    success: function (obj) {
                        if (obj.code == 200) {
                            layer.msg("删除成功", {icon: 1.5});
                            //加载表格
                            tableIns.reload();
                        } else {
                            layer.msg(obj.msg);
                        }
                    },
                });
            });
        } else if(obj.event === 'edit'){
            openAddOrUpdateUserDialog(data.id);
        }
    });
});
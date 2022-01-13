layui.use(['table', 'layer'], function () {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 营销机会列表展示
     */
    var tableIns = table.render({
        elem: '#saleChanceList', // 表格绑定的ID
        url: ctx + '/sale_chance/list', // 访问数据的地址
        cellMinWidth: 95,
        page: true, // 开启分页
        height: "full-125",
        limits: [10, 15, 20, 25],
        limit: 10,
        toolbar: "#toolbarDemo",
        id: "saleChanceListTable",
        cols: [[
            {type: "checkbox", fixed: "center"},
            {field: "id", title: '编号', fixed: "true"},
            {field: 'chanceSource', title: '机会来源', align: "center"},
            {field: 'customerName', title: '客户名称', align: 'center'},
            {field: 'cgjl', title: '成功几率', align: 'center'},
            {field: 'overview', title: '概要', align: 'center'},
            {field: 'linkMan', title: '联系人', align: 'center'},
            {field: 'linkPhone', title: '联系电话', align: 'center'},
            {field: 'description', title: '描述', align: 'center'},
            {field: 'createMan', title: '创建人', align: 'center'},
            {field: 'createDate', title: '创建时间', align: 'center'},
            {field: 'uname', title: '指派人', align: 'center'},
            {field: 'assignTime', title: '分配时间', align: 'center'},
            {
                field: 'state', title: '分配状态', align: 'center', templet: function (d) {
                    return formatterState(d.state);
                }
            },
            {
                field: 'devResult', title: '开发状态',
                align: 'center', templet: function (d) {
                    return formatterDevResult(d.devResult);
                }
            },
            {
                title: '操作',
                templet: '#saleChanceListBar', fixed: "right", align: "center", minWidth: 150
            }
        ]]
    });

    /**
     * 格式化分配状态
     * 0 - 未分配
     * 1 - 已分配
     * 其他 - 未知
     * @param state
     * @returns {string}
     */
    function formatterState(state) {
        if (state == 0) {
            return "<div style='color: #ff7700'>未分配</div>";
        } else if (state == 1) {
            return "<div style='color: green'>已分配</div>";
        } else {
            return "<div style='color: red'>未知</div>";
        }
    }

    /**
     * 格式化开发状态
     * 0 - 未开发
     * 1 - 开发中
     * 2 - 开发成功
     * 3 - 开发失败
     * @param value
     * @returns {string}
     */
    function formatterDevResult(value) {
        if (value == 0) {
            return "<div style='color: #ff7700'>未开发</div>";
        } else if (value == 1) {
            return "<div style='color: #00FF00;'>开发中</div>";
        } else if (value == 2) {
            return "<div style='color: #00B83F'>开发成功</div>";
        } else if (value == 3) {
            return "<div style='color: red'>开发失败</div>";
        } else {
            return "<div style='color: #af0000'>未知</div>"
        }
    }

    /**
     * 绑定搜索按钮的点击事件
     */
    $(".search_btn").click(function () {
        table.reload('saleChanceListTable', {
            where: {
                //设定参数
                customerName: $("input[name='customerName']").val(),//客户名
                createMan: $("input[name='creatrMan']").val(),//创建人
                state: $("#state").val()//状态
            },
            page: {
                curr: 1,//重新加载从第一页开始
            }
        });
    });
    /**
     * 头部工具栏 监听事件
     */
    table.on('toolbar(saleChances)', function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        switch (obj.event) {
            case 'add':
                //点击添加按钮，打开营销机会对话框
                openAddOrUpdateSaleChanceDialog();
                break;
            case 'del':
                // 点击删除按钮，将对应选中的记录删除(批量删除)
                deleteSaleChance(checkStatus.data);
        }
        ;
    });

    function deleteSaleChance(data) {
        //判断数据是否为0
        if (data.lengh == 0) {
            layer.msg("请选择要删除的数据");
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
                url: ctx + "/sale_chance/delete",
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
    };

    /**
     * 打开营销机会的对话框
     */
    function openAddOrUpdateSaleChanceDialog(saleChanceId) {
        var title = "<h2>营销机会管理 - 机会添加</h2>";
        var url = ctx + "/sale_chance/addOrUpdateSaleChancePage";
        // 通过id判断是添加操作还是修改操作
        if (saleChanceId) {
            // 如果id不为空，则为修改操作
            title = "<h2>营销机会管理 - 机会更新</h2>";
            url = url + "?id=" + saleChanceId;
        }
        layui.layer.open({
            title: title,
            type: 2,//iframe
            content: url,
            area: ["500px", "620px"],
            maxmin: true
        });
    }

    /**
     * 表格行 监听事件
     * saleChances为table标签的lay-filter 属性值
     */
    table.on('tool(saleChances)', function (obj) {
        var data = obj.data; // 获得当前行数据
        var layEvent = obj.event; // 获得 lay-event 对应的值（也可以是表头的 event 参数对应的值)
        // 判断事件类型
        if (layEvent === 'edit') { // 编辑操作
            // 获取当前要修改的行的id
            var saleChanceId = data.id;
            // 点击表格行的编辑按钮，打开更新营销机会的对话框
            openAddOrUpdateSaleChanceDialog(saleChanceId);
        } else if (layEvent === 'del') {//单个删除操作
            //询问用户是否删除
            layer.confirm("您确定要删除这条数据吗", {btn: ["赶紧删", "不删了"],}, function (index) {
                //关闭确认框
                layer.close(index);
                //发送ajax请求
                $.ajax({
                    type: "post",
                    url: ctx + "/sale_chance/delete",
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
        }
    });
});
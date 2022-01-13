var zTreeobj;
$(function () {
    loadModuleInfo();
});

function loadModuleInfo() {
    //发送请求查询所有资源信息
    $.ajax({
        type: "post",
        url: ctx + "/module/findModules",
        dataType: "json",
        data:{"roleId":$("#roleId").val()},
        success: function (data) {
            var setting = {
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                view: {
                    showLine: false
                    // showIcon: false
                },
                check: {
                    enable: true,
                    chkboxType: {"Y": "ps", "N": "ps"}
                },
                callback:{
                    onCheck:zTreeOnCheck
                }
            };
            var zNodes = data;
            zTreeObj = $.fn.zTree.init($("#test1"), setting, zNodes);
        }
    });
}
function zTreeOnCheck(event, treeId, treeNode) {
    var nodes= zTreeObj.getCheckedNodes(true);
    var roleId=$("#roleId").val();
    var mids="mids=";
    for(var i=0;i<nodes.length;i++){
        if(i<nodes.length-1){
            mids=mids+nodes[i].id+"&mids=";
        }else{
            mids=mids+nodes[i].id;
        }
    }
    $.ajax({
        type:"post",
        url:ctx+"/role/addGrant",
        data:mids+"&roleId="+roleId,
        dataType:"json",
        success:function (data) {
            alert(data.msg);
        }
    });
};
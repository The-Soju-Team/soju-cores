searchArea = function() {
    $('#areaTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,       
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searcharea',
            data: function ( d ) {
                d.areaName = $('#areaName').val();
                if(document.getElementById('isdelete').value == '1') {
                    d.isdelete = '1';
                    d.areaId = $('#areaTable_wrapper :input[type="checkbox"]').serialize();
                }
                else d.isdelete = '0';
            },
            cache: false
        },
        initComplete: function(settings, json) {
            if(document.getElementById('isdelete').value == '1') {
                successMessage('Xóa thành công!');
            }
            document.getElementById('isdelete').value = '0';
            validateCheckAll();
        },        
        language: {
            'decimal':        '',
            'emptyTable':     'Không có dữ liệu',
            'info':           'Từ _START_ đến _END_ của _TOTAL_ bản ghi',
            'infoEmpty':      'Từ 0 đến 0 của 0 bản ghi',
            'infoFiltered':   '(Lọc từ _MAX_ tổng số bản ghi)',
            'infoPostFix':    '',
            'thousands':      ',',
            'lengthMenu':     'Hiển thị _MENU_ dòng/trang',
            'loadingRecords': 'Đang tải...',
            'processing':     'Đang xử lý...',
            'search':         'Tìm kiếm:',
            'zeroRecords':    'Không tìm thấy dữ liệu',
            'paginate': {
                'first':      'Đầu',
                'last':       'Cuối',
                'next':       'Tiếp',
                'previous':   'Trước'
            },
            'aria': {
                'sortAscending':  ': Sắp xếp tăng dần',
                'sortDescending': ': Sắp xếp giảm dần'
            }
        }
    } );
}

checkAll = function (obj) {
    var arrCheckbox = document.getElementsByName('areaId');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('areaId');
    var checkboxAll = document.getElementById('selectAll')
    checkboxAll.checked = true;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(!arrCheckbox[i].checked) {
            checkboxAll.checked = false;
            break;
        }
    }    
}

deleteArea = function() {
    document.getElementById('isdelete').value = '1';
    searchArea();
}

viewAddArea = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewaddarea',
        data: {},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#addForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    addArea();
                    return false;
                } else {
                    return true;
                }
            });
            document.getElementById('areaName').focus();
        }
    });
}

deleteOnClick = function() {
    var arrCheckbox = document.getElementsByName('areaId');
    var check = false;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(arrCheckbox[i].checked) {
            check = true;
            break;
        }
    }
    if(check) confirmDelete();
    else errorMessage('Bạn chưa chọn bản ghi để xóa!');
}

confirmDelete = function() {
    noty({
        text: 'Bạn có muốn xóa không?',
        layout: 'center',
        type: 'info',
        buttons: [
                {addClass: 'btn btn-info btn-clean', text: 'Đồng ý', onClick: function($noty) {
                    $noty.close();
                    deleteArea();
                }},
                {addClass: 'btn btn-info btn-clean', text: 'Hủy bỏ', onClick: function($noty) {
                    $noty.close();
                }}
            ]
    });    
}

errorMessage = function(text) {
    noty({
        text: text,
        layout: 'center',
        type: 'info',
        buttons: [
                {addClass: 'btn btn-info btn-clean', text: 'Đóng', onClick: function($noty) {
                    $noty.close();
                }}
            ]
    });    
}

successMessage = function(text) {
    noty({
        text: text,
        layout: 'center',
        type: 'success'
    });  
    setTimeout(function(){ closeAllMessage(); }, 2000);
}

addArea = function() {
    if(validateClient()) {
        $.ajax({ 
            type: 'POST',
            dataType: 'json',
            url: '/addarea',
            data: $("#addForm").serialize(),
            cache: false, //fix loop IE
            success: function(data, textStatus, jqXHR) {
                closeAllMessage();
                successMessage('Thêm mới thành công!');
                clearForm();
            }
        });
    }
}

loadViewArea = function(viewAreaId) {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/loadviewarea',
        data: {areaId: viewAreaId},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            var parentPage = document.getElementById('parent').value;
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#updateForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    updateArea();
                    return false;
                } else {
                    return true;
                }
            });             
            if(data.area.area_id) document.getElementById('areaid').value = data.area.area_id;
            if(data.area.area_name) document.getElementById('lblareaname').innerHTML = data.area.area_name;
        }
    });    
}

editArea = function() {
    var obj = document.getElementById('editButton');
    if(obj.innerHTML == '<span class="fa fa-pencil"></span> Sửa') {
        document.getElementById('lblareaname').style.display = 'none';
        obj.innerHTML = '<span class="fa fa-close"></span> Hủy bỏ';
        document.getElementById('saveButton').style.display = '';
        document.getElementById('areaname').style.display = '';
        document.getElementById('areaname').value = document.getElementById('lblareaname').innerHTML;
        document.getElementById('areaname').focus();
    } else {
        document.getElementById('lblareaname').style.display = '';
        obj.innerHTML = '<span class="fa fa-pencil"></span> Sửa';
        document.getElementById('saveButton').style.display = 'none';
        document.getElementById('areaname').style.display = 'none';
    }
}

updateArea = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/updatearea',
        data: $("#updateForm").serialize(),
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            closeAllMessage();
            document.getElementById('lblareaname').innerHTML = document.getElementById('areaname').value;
            editArea();
            successMessage('Ghi lại thành công!');
        }
    });         
}


validateClient = function() {
    closeAllMessage();
    if(document.getElementById('areaName').value == null || document.getElementById('areaName').value.trim().length == 0) {
        showFieldError('areaName','Hãy nhập tên khu vực');
        return false;
    }
    return true;
}

closeAllMessage = function() {
    $.noty.closeAll();
    closeAllFieldError();
}

showFieldError = function(fieldId, message) {
    document.getElementById(fieldId).className = 'form-control error';
    document.getElementById(fieldId + '-require').style.display = '';
    document.getElementById(fieldId + '-require').innerHTML = message;
}

closeAllFieldError = function() {
    var arrControl = document.getElementsByClassName('form-control');
    var arrErrorLabel = document.getElementsByClassName('error');
    for(var i = 0; i < arrControl.length; i++) arrControl[i].className = 'form-control';
    for(var i = 0; i < arrErrorLabel.length; i++) arrErrorLabel[i].style.display = 'none';
}

clearForm = function() {
    document.getElementById('areaName').value = '';
}

backToList = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/backlistarea',
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#searchForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    searchArea();
                    return false;
                } else {
                    return true;
                }
            });            
            searchArea();
        }
    }); 
}

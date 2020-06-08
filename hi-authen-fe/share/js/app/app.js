searchApp = function() {
    $('#appTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,       
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/authen/app',
            data: function ( d ) {
                d['hi-process'] = 'app';
                d.cmd = 'SEARCH_APP';
                d.appname = $('#s_appname').val();
                d.appcode = $('#s_appcode').val();
                if(document.getElementById('isdelete').value == '1') {
                    d.isdelete = '1';
                    d.appid = $('#appTable_wrapper :input[type="checkbox"]').serialize();
                }
                else {
                    d.isdelete = '0';
                }
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
    var arrCheckbox = document.getElementsByName('appid');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('appid');
    var checkboxAll = document.getElementById('selectAll');
    if(checkboxAll) {
        checkboxAll.checked = true;
        for(var i = 0; i < arrCheckbox.length; i++) {
            if(!arrCheckbox[i].checked) {
                checkboxAll.checked = false;
                break;
            }
        }    
    }
}

deleteApp = function() {
    document.getElementById('isdelete').value = '1';
    searchApp();
}

deleteOnClick = function() {
    var arrCheckbox = document.getElementsByName('appid');
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
                    deleteApp();
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

addApp = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/authen/app',
        data: $("#addForm").serialize(),
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            closeAllMessage();
            searchApp();
            if(document.getElementById('cmd') == 'ADD_APP') {
                successMessage('Thêm mới thành công!');
                clearForm();
            } else {
                successMessage('Cập nhật thành công!');
            }
        }
    });
}

validateClient = function() {
    closeAllMessage();
    if(document.getElementById('appname').value == null || document.getElementById('appname').value.trim().length == 0) {
        showFieldError('appname','Hãy nhập tài khoản');
        return false;
    } else if(document.getElementById('password').value == null || document.getElementById('password').value.trim().length == 0) {
        showFieldError('password','Hãy nhập Mật khẩu');
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
    document.getElementById('appname').value = '';
    document.getElementById('appcode').value = '';
    document.getElementById('ip').value = '';
    document.getElementById('port').value = '';
}

loadViewApp = function(viewAppId) {
    $('#selectModal').modal().show();
    document.getElementById('cmd').value = 'UPDATE_APP';
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/authen/app',
        data: {appid: viewAppId, 'hi-process': 'app', cmd: 'LOAD_VIEW_APP'},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            if(data.app_id) document.getElementById('appid').value = data.app_id;
            if(data.app_name) document.getElementById('appname').value = data.app_name;
            if(data.app_code) document.getElementById('appcode').value = data.app_code;
            if(data.ip) document.getElementById('ip').value = data.ip;
            if(data.port) document.getElementById('port').value = data.port;
        }
    });    
}

function viewAddApp() {
    document.getElementById('cmd').value = 'ADD_APP';
    clearForm();
    $('#selectModal').modal().show();
}

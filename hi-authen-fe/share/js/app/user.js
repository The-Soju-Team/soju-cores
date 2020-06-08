searchUser = function() {
    $('#fromdate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    $('#todate').datepicker({pickTime: false, format: "dd-mm-yyyy" });     
    $('#userTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,       
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/authen/user',
            data: function ( d ) {
                d['hi-process'] = 'user';
                d.cmd = 'SEARCH_USER';
                d.username = $('#s_username').val();
                d.mobile = $('#s_mobile').val();
                d.fullname = $('#s_fullname').val();
                d.usertype = $('#s_usertype').val();      
                d.fromdate = $('#s_fromdate').val();
                d.todate = $('#s_todate').val();      
                if(document.getElementById('isdelete').value == '1') {
                    d.isdelete = '1';
                    d.userid = $('#userTable_wrapper :input[type="checkbox"]').serialize();
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
    var arrCheckbox = document.getElementsByName('userid');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('userid');
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

deleteUser = function() {
    document.getElementById('isdelete').value = '1';
    searchUser();
}

deleteOnClick = function() {
    var arrCheckbox = document.getElementsByName('userid');
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
                    deleteUser();
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

addUser = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/authen/user',
        data: $("#addForm").serialize(),
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            closeAllMessage();
            searchUser();
            if(document.getElementById('cmd') == 'ADD_USER') {
                successMessage('Thêm mới thành công!');
                clearForm();
            } else {
                successMessage('Cập nhật thành công!');
            }
        }
    });
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
    document.getElementById('username').value = '';
    document.getElementById('password').value = '';
    document.getElementById('mobile').value = '';
    document.getElementById('birthday').value = '';
    document.getElementById('password').value = '';
    document.getElementById('usertype').value = '0';
    document.getElementById('fullname').value = '';
    document.getElementById('email').value = '';
}

loadViewUser = function(viewUserId) {
    $('#selectModal').modal().show();
    document.getElementById('cmd').value = 'UPDATE_USER';
    
    loadAppData();
    
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/authen/user',
        data: {userid: viewUserId, 'hi-process': 'user', cmd: 'LOAD_VIEW_USER'},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            $('#birthday').datepicker({pickTime: true, format: "dd-mm-yyyy" });
            if(data.user_id) document.getElementById('userid').value = data.user_id;
            if(data.user_name) document.getElementById('username').value = data.user_name;
            if(data.msisdn) document.getElementById('mobile').value = data.msisdn;
            if(data.birthday) document.getElementById('birthday').value = data.birthday;
            if(data.full_name) document.getElementById('fullname').value = data.full_name;
            if(data.user_type) document.getElementById('usertype').value = data.user_type;
            if(data.email) document.getElementById('email').value = data.email;
            searchUserApp();
        }
    });    
}

function searchUserApp() {
    $('#appTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: false,
        searching: false,    
        bSort: false,
        bPaging: false,
        columnDefs: [ 
            {
                targets: 2,
                render: function (data, type, full, meta) {
                    data = '<button type="button" class="btn btn-danger" onclick="deleteApp(' + full[0] + ');" style="float:right; padding-left: 10px; padding-right: 5px"><span class="fa fa-times"></span><input type="hidden" name="appid" value="' + data + '"/></button>';
                    return data;
                }
            }          
        ],                  
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/authen/app',
            data: function ( d ) {
                d['hi-process'] = 'app';
                d.cmd = 'GET_APP_BY_USER';
                d.userid = $('#userid').val();
            },
            cache: false
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

function loadAppData() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/authen/user',
        data: {'hi-process': 'user', cmd: 'LOAD_APP_DATA'},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            var innerData = '';
            for(var i = 0; i < data.apps.length; i++) {
                innerData += '<option value="' + data.apps[i].app_id + '">' + data.apps[i].app_name + '</option>';
            }
            document.getElementById('app').innerHTML = innerData;
        }
    });    
}

function viewAddUser() {
    $('#birthday').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    loadAppData();
    searchUserApp();
    document.getElementById('cmd').value = 'ADD_USER';
    clearForm();
    $('#selectModal').modal().show();
}

function addApp() {
    var selectApp = document.getElementById('app');
    if(selectApp.value != 0) {
        var t = $('#appTable').DataTable();
        t.row.add( [
                t.column(0).data().length + 1,
                selectApp.options[selectApp.selectedIndex].text,
                selectApp.value
            ] ).draw( false );
    }
}

function deleteApp(index) {
    var t = $('#appTable').DataTable();
    t.row(index - 1).remove().draw(false);
    var tableSize = t.column(0).data().length;
    for (var i = 0; i < tableSize; i++) {
        var cell = t.cell(i,0);
        cell.data('' + (i+1));
    }
}

searchCategory = function() {
    $('#categoryTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchcategory',
            data: function ( d ) {
                d.categoryCode = $('#timeLength').val();
                d.categoryName = $('categoryName').val();
                if(document.getElementById('isdelete').value == '1') {
                    d.isdelete = '1';
                    d.categoryId = $('#categoryTable_wrapper :input[type="checkbox"]').serialize();
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
    var arrCheckbox = document.getElementsByName('categoryId');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('categoryId');
    var checkboxAll = document.getElementById('selectAll')
    checkboxAll.checked = true;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(!arrCheckbox[i].checked) {
            checkboxAll.checked = false;
            break;
        }
    }    
}

deleteCategory = function() {
    document.getElementById('isdelete').value = '1';
    searchCategory();
}

viewAddCategory = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewaddcategory',
        data: {},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#addForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    addCategory();
                    return false;
                } else {
                    return true;
                }
            });
            document.getElementById('categoryName').focus();
        }
    });
}

deleteOnClick = function() {
    var arrCheckbox = document.getElementsByName('categoryId');
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
                    deleteCategory();
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

addCategory = function() {
    if(validateClient()) {
        $.ajax({ 
            type: 'POST',
            dataType: 'json',
            url: '/addcategory',
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

validateClient = function() {
    closeAllMessage();
    if(document.getElementById('categoryName').value == null || document.getElementById('categoryName').value.trim().length == 0) {
        showFieldError('timeLength','Hãy nhập Thời lượng');
        return false;
    } else if(document.getElementById('timeLength').value == null || document.getElementById('timeLength').value.trim().length == 0) {
        showFieldError('timeLength','Hãy nhập Thời lượng');
        return false;
    } else if(document.getElementById('hourPrice').value == null || document.getElementById('hourPrice').value.trim().length == 0) {
        showFieldError('hourPrice','Hãy nhập Đơn giá');
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
    document.getElementById('categoryName').value = '';
    document.getElementById('timeLength').value = '';
    document.getElementById('hourPrice').value = '';
}

backToList = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/backlistcategory',
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#searchForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    searchCategory();
                    return false;
                } else {
                    return true;
                }
            });            
            searchCategory();
        }
    }); 
}
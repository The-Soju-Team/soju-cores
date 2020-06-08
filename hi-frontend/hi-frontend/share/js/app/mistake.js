searchMistake = function() {
    $('#mistakeTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,       
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchmistake',
            data: function ( d ) {
                d.name = $('#name').val();
                if(document.getElementById('isdelete').value == '1') {
                    d.isdelete = '1';
                    d.mistakeId = $('#mistakeTable_wrapper :input[type="checkbox"]').serialize();
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
            'loadingMistakes': 'Đang tải...',
            'processing':     'Đang xử lý...',
            'search':         'Tìm kiếm:',
            'zeroMistakes':    'Không tìm thấy dữ liệu',
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
    var arrCheckbox = document.getElementsByName('mistakeId');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('mistakeId');
    var checkboxAll = document.getElementById('selectAll')
    checkboxAll.checked = true;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(!arrCheckbox[i].checked) {
            checkboxAll.checked = false;
            break;
        }
    }    
}


loadViewMistake = function(viewMistakeId) {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/loadviewmistake',
        data: {mistakeId: viewMistakeId},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            var parentPage = document.getElementById('parent').value;
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#updateForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    updateMistake();
                    return false;
                } else {
                    return true;
                }
            });             
            if(data.mistake.mistake_id) document.getElementById('mistakeid').value = data.mistake.mistake_id;
            if(data.mistake.name) document.getElementById('lblmistakename').innerHTML = data.mistake.name;
            if(data.mistake.reward_amount) document.getElementById('lblamount').innerHTML = data.mistake.reward_amount;
            if(data.mistake.description) document.getElementById('lbldescription').innerHTML = data.mistake.description;
        }
    });    
}

editMistake = function() {
    var obj = document.getElementById('editButton');
    if(obj.innerHTML == '<span class="fa fa-pencil"></span> Sửa') {
        document.getElementById('lblmistakename').style.display = 'none';
        document.getElementById('lblamount').style.display = 'none';
        document.getElementById('lbldescription').style.display = 'none';
        obj.innerHTML = '<span class="fa fa-close"></span> Hủy bỏ';
        
        document.getElementById('saveButton').style.display = '';

        document.getElementById('mistakename').style.display = '';
        document.getElementById('mistakename').value = document.getElementById('lblmistakename').innerHTML;
        document.getElementById('amount').style.display = '';
        document.getElementById('amount').value = document.getElementById('lblamount').innerHTML;
        document.getElementById('description').style.display = '';
        document.getElementById('description').value = document.getElementById('lbldescription').innerHTML;
        
        document.getElementById('mistakename').focus();
    } else {
        document.getElementById('lblmistakename').style.display = '';
        document.getElementById('lblamount').style.display = '';
        document.getElementById('lbldescription').style.display = '';
        
        obj.innerHTML = '<span class="fa fa-pencil"></span> Sửa';
        
        document.getElementById('saveButton').style.display = 'none';
        document.getElementById('mistakename').style.display = 'none';
        document.getElementById('amount').style.display = 'none';
        document.getElementById('description').style.display = 'none';
    }
}

updateMistake = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/updatemistake',
        data: $("#updateForm").serialize(),
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            closeAllMessage();
            document.getElementById('lblmistakename').innerHTML = document.getElementById('mistakename').value;
            document.getElementById('lblamount').innerHTML = document.getElementById('amount').value;
            document.getElementById('lbldescription').innerHTML = document.getElementById('description').value;
            editMistake();
            successMessage('Ghi lại thành công!');
        }
    });         
}

deleteMistake = function() {
    document.getElementById('isdelete').value = '1';
    searchMistake();
}

viewAddMistake = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewaddmistake',
        data: {},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#addForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    addMistake();
                    return false;
                } else {
                    return true;
                }
            });
            document.getElementById('name').focus();
        }
    });
}

deleteOnClick = function() {
    var arrCheckbox = document.getElementsByName('mistakeId');
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
                    deleteMistake();
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

addMistake = function() {
    if(validateClient()) {
        $.ajax({ 
            type: 'POST',
            dataType: 'json',
            url: '/addmistake',
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
    if(document.getElementById('name').value == null || document.getElementById('name').value.trim().length == 0) {
        showFieldError('name','Hãy nhập Tên lỗi vi phạm');
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
    document.getElementById('name').value = '';
    document.getElementById('description').value = '';
}

backToList = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/backlistmistake',
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#searchForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    searchMistake();
                    return false;
                } else {
                    return true;
                }
            });            
            searchMistake();
        }
    }); 
}

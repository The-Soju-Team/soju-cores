searchPromotion = function() {
    $('#fromdate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    $('#todate').datepicker({pickTime: false, format: "dd-mm-yyyy" });     
    $('#promotionTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,       
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchpromotion',
            data: function ( d ) {
                d.promotionCode = $('#promotionCode').val();
                d.content = $('#content').val();
                d.time = $('#time').val();
                d.status = $('#status').val();      
                d.fromdate = $('#fromdate').val();
                d.todate = $('#todate').val();      
                if(document.getElementById('isdelete').value == '1') {
                    d.isdelete = '1';
                    d.promotionId = $('#promotionTable_wrapper :input[type="checkbox"]').serialize();
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
    var arrCheckbox = document.getElementsByName('promotionId');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('promotionId');
    var checkboxAll = document.getElementById('selectAll')
    checkboxAll.checked = true;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(!arrCheckbox[i].checked) {
            checkboxAll.checked = false;
            break;
        }
    }    
}

deletePromotion = function() {
    document.getElementById('isdelete').value = '1';
    searchPromotion();
}

viewAddPromotion = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewaddpromotion',
        data: {},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#fromDate').datepicker({pickTime: true, format: "dd-mm-yyyy" });
            $('#toDate').datepicker({pickTime: true, format: "dd-mm-yyyy" });
            $('#addForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    addPromotion();
                    return false;
                } else {
                    return true;
                }
            });
            var innerArea = '<option value="0"></option>';
            for(var i = 0; i < data.areas.length; i++) {
                innerArea += '<option value="' + data.areas[i].area_id + '">' + data.areas[i].area_name + '</option>';
            }
            document.getElementById('areaId').innerHTML = innerArea;
            document.getElementById('promotionCode').focus();
        }
    });
}

deleteOnClick = function() {
    var arrCheckbox = document.getElementsByName('promotionId');
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
                    deletePromotion();
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

addPromotion = function() {
    if(validateClient()) {
        $.ajax({ 
            type: 'POST',
            dataType: 'json',
            url: '/addpromotion',
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
    if(document.getElementById('promotionCode').value == null || document.getElementById('promotionCode').value.trim().length == 0) {
        showFieldError('promotionCode','Hãy nhập Mã chính sách');
        return false;
    } else if(document.getElementById('fromDate').value == null || document.getElementById('fromDate').value.trim().length == 0) {
        showFieldError('fromDate','Hãy nhập Áp dụng từ ngày');
        return false;
    } else if(document.getElementById('toDate').value == null || document.getElementById('toDate').value.trim().length == 0) {
        showFieldError('toDate','Hãy nhập Áp dụng đến ngày');
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
    document.getElementById('promotionCode').value = '';
    document.getElementById('content').value = '';
    document.getElementById('timeFrom').value = '';
    document.getElementById('timeTo').value = '';
    document.getElementById('fromDate').value = '';
    document.getElementById('toDate').value = '';
    document.getElementById('areaId').value = '';
    document.getElementById('status').value = '';
}

viewPromotion = function(viewPromotionId) {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewpromotion',
        data: {promotionId: viewPromotionId},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#fromDate').datepicker({pickTime: true, format: "dd-mm-yyyy" });
            $('#toDate').datepicker({pickTime: true, format: "dd-mm-yyyy" });
            $('#updateForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    updatePromotion();
                    return false;
                } else {
                    return true;
                }
            });
            var innerArea = '<option value="0"></option>';
            for(var i = 0; i < data.areas.length; i++) {
                innerArea += '<option value="' + data.areas[i].area_id + '">' + data.areas[i].area_name + '</option>';
            }
            document.getElementById('areaId').innerHTML = innerArea;            
            if(data.promotion.promotion_id) document.getElementById('promotionId').value = data.promotion.promotion_id;
            if(data.promotion.promotion_code) document.getElementById('lblPromotionCode').innerHTML = data.promotion.promotion_code;
            if(data.promotion.content) document.getElementById('lblContent').innerHTML = data.promotion.content;
            if(data.promotion.time_from || data.promotion.time_from === 0) document.getElementById('lblTimeFrom').innerHTML = data.promotion.time_from;
            if(data.promotion.time_to || data.promotion.time_to === 0) document.getElementById('lblTimeTo').innerHTML = data.promotion.time_to;
            if(data.promotion.from_date) document.getElementById('lblFromDate').innerHTML = data.promotion.from_date;
            if(data.promotion.to_date) document.getElementById('lblToDate').innerHTML = data.promotion.to_date;
            if(data.promotion.create_date) document.getElementById('lblCreateDate').innerHTML = data.promotion.create_date;
            if(data.promotion.status == '1') document.getElementById('lblStatus').innerHTML = 'Chưa áp dụng';
            else if(data.promotion.status == '2') document.getElementById('lblStatus').innerHTML = 'Đang áp dụng';
            else if(data.promotion.status == '3') document.getElementById('lblStatus').innerHTML = 'Hết hiệu lực';
            if(data.promotion.area_id) {
                var cboAreaId = document.getElementById('areaId');
                cboAreaId.value = data.promotion.area_id;
                document.getElementById('lblAreaId').innerHTML = cboAreaId.options[cboAreaId.selectedIndex].innerHTML;
            }
        }
    });    
}

editPromotion = function() {
    var obj = document.getElementById('editButton');
    var divFrom = document.getElementById('fromDate').parentNode;
    var divTo = document.getElementById('toDate').parentNode;
    
    if(obj.innerHTML == '<span class="fa fa-pencil"></span> Sửa') {
        document.getElementById('lblPromotionCode').style.display = 'none';
        document.getElementById('lblContent').style.display = 'none';
        document.getElementById('lblTimeFrom').style.display = 'none';
        document.getElementById('lblTimeTo').style.display = 'none';
        document.getElementById('lblFromDate').style.display = 'none';
        document.getElementById('lblToDate').style.display = 'none';
        document.getElementById('lblStatus').style.display = 'none';
        document.getElementById('lblAreaId').style.display = 'none';
        
        document.getElementById('divCreateDate').style.display = 'none';
        obj.innerHTML = '<span class="fa fa-close"></span> Hủy bỏ';
        document.getElementById('saveButton').style.display = '';

        document.getElementById('promotionCode').style.display = '';
        document.getElementById('promotionCode').value = document.getElementById('lblPromotionCode').innerHTML;
        document.getElementById('content').style.display = '';
        document.getElementById('content').value = document.getElementById('lblContent').innerHTML;
        document.getElementById('timeFrom').style.display = '';
        document.getElementById('timeFrom').value = document.getElementById('lblTimeFrom').innerHTML;
        document.getElementById('timeTo').style.display = '';
        document.getElementById('timeTo').value = document.getElementById('lblTimeTo').innerHTML;
        divFrom.style.display = '';
        document.getElementById('fromDate').value = document.getElementById('lblFromDate').innerHTML;
        divFrom.style.display = '';
        document.getElementById('toDate').value = document.getElementById('lblToDate').innerHTML;
        document.getElementById('areaId').style.display = '';
        document.getElementById('status').style.display = '';
        if(document.getElementById('lblStatus').innerHTML == 'Chưa áp dụng') document.getElementById('status').value = '1';
        else if(document.getElementById('lblStatus').innerHTML == 'Đang áp dụng') document.getElementById('status').value = '2';
        else if(document.getElementById('lblStatus').innerHTML == 'Hết hiệu lực') document.getElementById('status').value = '3';
        
        document.getElementById('promotionCode').focus();
    } else {
        document.getElementById('lblPromotionCode').style.display = '';
        document.getElementById('lblContent').style.display = '';
        document.getElementById('lblTimeFrom').style.display = '';
        document.getElementById('lblTimeTo').style.display = '';
        document.getElementById('lblFromDate').style.display = '';
        document.getElementById('lblToDate').style.display = '';
        document.getElementById('lblStatus').style.display = '';
        document.getElementById('lblAreaId').style.display = '';
        document.getElementById('divCreateDate').style.display = '';
        
        obj.innerHTML = '<span class="fa fa-pencil"></span> Sửa';
        
        document.getElementById('saveButton').style.display = 'none';
        document.getElementById('promotionCode').style.display = 'none';
        document.getElementById('content').style.display = 'none';
        document.getElementById('timeFrom').style.display = 'none';
        document.getElementById('timeTo').style.display = 'none';
        divFrom.style.display = 'none';
        divTo.style.display = 'none';
        document.getElementById('status').style.display = 'none';
        document.getElementById('areaId').style.display = 'none';
    }
}

updatePromotion = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/updatepromotion',
        data: $("#updateForm").serialize(),
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            closeAllMessage();
            document.getElementById('lblPromotionCode').innerHTML = document.getElementById('promotionCode').value;
            document.getElementById('lblContent').innerHTML = document.getElementById('content').value;
            document.getElementById('lblTimeFrom').innerHTML = document.getElementById('timeFrom').value;
            document.getElementById('lblTimeTo').innerHTML = document.getElementById('timeTo').value;
            document.getElementById('lblFromDate').innerHTML = document.getElementById('fromDate').value;
            document.getElementById('lblToDate').innerHTML = document.getElementById('toDate').value;
            
            if(document.getElementById('status').value == '1') document.getElementById('lblStatus').innerHTML = 'Chưa áp dụng';
            else if(document.getElementById('status').value == '2') document.getElementById('lblStatus').innerHTML = 'Đang áp dụng';
            else if(document.getElementById('status').value == '3') document.getElementById('lblStatus').innerHTML = 'Hết hiệu lực';

            var cboAreaId = document.getElementById('areaId');
            document.getElementById('lblAreaId').innerHTML = cboAreaId.options[cboAreaId.selectedIndex].innerHTML;

            editPromotion();
            successMessage('Ghi lại thành công!');
        }
    });         
}

backToList = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/backlistpromotion',
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#searchForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    searchPromotion();
                    return false;
                } else {
                    return true;
                }
            });            
            searchPromotion();
        }
    }); 
}

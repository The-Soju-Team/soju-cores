searchFundHistory = function() {
    $('#fromdate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    $('#todate').datepicker({pickTime: false, format: "dd-mm-yyyy" });     
            
    $('#fundHistoryTable').DataTable({
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,
        bSort: false,
        columnDefs: [
            {
                targets: 5,
                render: function (data, type, full, meta) {
                    if (data) return '<font style="color:#0571ce">' + separate(data.toString()) + '</font>';
                    return data;
                }
            },
            {
                targets: 6,
                render: function (data, type, full, meta) {
                    if (data) return '<font style="color:#0571ce">' + separate(data.toString()) + '</font>';
                    return data;
                }
            }             
        ],
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchfundhistory',
            data: function (d) {
                d.fundName = $('#fundName').val();
                d.receiptNo = $('#receiptNo').val();
                d.spendNo = $('#spendNo').val();
                d.fromdate = $('#fromdate').val();
                d.todate = $('#todate').val();
                d.type = $('#type').val();
            },
            cache: false
        },
        language: {
            'decimal': '',
            'emptyTable': 'Không có dữ liệu',
            'info': 'Từ _START_ đến _END_ của _TOTAL_ bản ghi',
            'infoEmpty': 'Từ 0 đến 0 của 0 bản ghi',
            'infoFiltered': '(Lọc từ _MAX_ tổng số bản ghi)',
            'infoPostFix': '',
            'thousands': ',',
            'lengthMenu': 'Hiển thị _MENU_ dòng/trang',
            'loadingRecords': 'Đang tải...',
            'processing': 'Đang xử lý...',
            'search': 'Tìm kiếm:',
            'zeroRecords': 'Không tìm thấy dữ liệu',
            'paginate': {
                'first': 'Đầu',
                'last': 'Cuối',
                'next': 'Tiếp',
                'previous': 'Trước'
            },
            'aria': {
                'sortAscending': ': Sắp xếp tăng dần',
                'sortDescending': ': Sắp xếp giảm dần'
            }
        }
    });
}

checkAll = function (obj) {
    var arrCheckbox = document.getElementsByName('messageId');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('messageId');
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

closeAllMessage = function() {
    $.noty.closeAll();
    closeAllFieldError();
}


clearForm = function() {
    $('#addForm input').val('');
}

separate = function(text) {
    var count = 0;
    for(var i = text.length - 1 ; i >= 0; i--) {
        count++;
        if(i != 0 && count % 3 === 0) {
            text = text.slice(0, i) + ',' + text.slice(i);
        }
    }
    return text;
}
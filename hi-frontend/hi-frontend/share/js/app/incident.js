searchIncident = function() {
    $('#fromdate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    $('#todate').datepicker({pickTime: false, format: "dd-mm-yyyy" });     

    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/searchincident',
        data: {},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            $('#incidentTable').DataTable({
                destroy: true,
                processing: false,
                serverSide: true,
                searching: false,
                bSort: false,
                columnDefs: [
                ],
                ajax: {
                    type: 'POST',
                    dataType: 'json',
                    url: '/searchincident',
                    data: function (d) {
                        d.content = $('#content').val();
                        d.senderName = $('#userName').val();
                        d.receiverName = $('#managerName').val();
                        d.fromdate = $('#fromdate').val();
                        d.todate = $('#todate').val();
                        d.status = $('#status').val();
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
    });    
}

checkAll = function (obj) {
    var arrCheckbox = document.getElementsByName('incidentId');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('incidentId');
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

errorIncident = function(text) {
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

successIncident = function(text) {
    noty({
        text: text,
        layout: 'center',
        type: 'success'
    });  
    setTimeout(function(){ closeAllIncident(); }, 2000);
}

closeAllIncident = function() {
    $.noty.closeAll();
    closeAllFieldError();
}


clearForm = function() {
    $('#addForm input').val('');
}

var hoursValue = ['','05:00','05:30','06:00','06:30','07:00','07:30','08:00','08:30','09:00','09:30','10:00','10:30','11:00','11:30','12:00','12:30','13:00','13:30','14:00','14:30','15:00','15:30','16:00','16:30','17:00','17:30','18:00','18:30','19:00','19:30','20:00','20:30','21:00','21:30','22:00','22:30'];
var innerHour = '';
for(var i = 0; i < hoursValue.length; i++) {
    innerHour += '<option value="' + hoursValue[i] + '">' + hoursValue[i] + '</option>';
}

loadData = function() {
    document.getElementById('time').innerHTML = innerHour;
    $('#date').datepicker({pickTime: true, format: "dd-mm-yyyy" });
}

searchUserMap = function() {
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
            url: '/searchusermap',
            data: function ( d ) {
                d.username = $('#username').val();
                d.mobile = $('#mobile').val();
                d.home = $('#home').val();
                d.usertype = $('#usertype').val();      
                d.fromdate = $('#fromdate').val();
                d.todate = $('#todate').val();
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

getLocation = function(uId) {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/getlocation',
        data: {
            userId : uId,
            date : document.getElementById('date').value,
            time : document.getElementById('time').value
        },
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            
        }
    });    
}
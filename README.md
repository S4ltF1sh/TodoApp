# Chức năng:

| Chức năng | Mô tả |
| --- | --- |
| [Tạo mới Todo](https://github.com/S4ltF1sh/TodoApp#t%E1%BA%A1o-m%E1%BB%9Bi-todo) | ![Tạo mới Todo](https://user-images.githubusercontent.com/84552830/192386174-1416b8f6-1cfe-4043-af0b-762f90ae2d4b.gif) |
| [Sửa Todo](https://github.com/S4ltF1sh/TodoApp#s%E1%BB%ADa-todo) | ![Sửa Todo](https://user-images.githubusercontent.com/84552830/192385410-c6a29545-e301-456e-b5a9-3b096450b12a.gif) |
| [Xoá và khôi phục Todo](https://github.com/S4ltF1sh/TodoApp#xo%C3%A1-v%C3%A0-kh%C3%B4i-ph%E1%BB%A5c-todo) | ![Xoá và khôi phục Todo](https://user-images.githubusercontent.com/84552830/192386217-e2682cd0-4b07-4e3c-88c5-9bd89cc01b75.gif) |
| [Tìm kiếm Todo](https://github.com/S4ltF1sh/TodoApp#t%C3%ACm-ki%E1%BA%BFm-todo) | ![Tìm kiếm Todo](https://user-images.githubusercontent.com/84552830/192385446-daad8d95-3a7e-4290-b20e-0a7b6fe567a7.gif) |
| [Chia sẻ Todo](https://github.com/S4ltF1sh/TodoApp#chia-s%E1%BA%BB-todo) | ![Chia sẻ Todo](https://user-images.githubusercontent.com/84552830/192385543-c282f12d-f6ef-4066-82bb-0655f0670699.gif) |
| [Một số thao tác với Group](https://github.com/S4ltF1sh/TodoApp#m%E1%BB%99t-s%E1%BB%91-thao-t%C3%A1c-v%E1%BB%9Bi-group) | ![Một số thao tác với Group (2)](https://user-images.githubusercontent.com/84552830/192385591-5403eb2b-e4c6-4648-9c5f-fa448b64a0c0.gif) |
| [Multi Select](https://github.com/S4ltF1sh/TodoApp#multi-select) | ![Multi Select](https://user-images.githubusercontent.com/84552830/192385656-34b7a055-2ff9-4ee6-9461-f01536568848.gif) |
| [Thêm và nhận nhắc nhở](https://github.com/S4ltF1sh/TodoApp#th%C3%AAm-v%C3%A0-nh%E1%BA%ADn-nh%E1%BA%AFc-nh%E1%BB%9F) | ![Thêm nhắc nhở](https://user-images.githubusercontent.com/84552830/192385839-8dd4fe63-8ced-4b17-8bab-9b0541f5c792.gif) |
||![Nhận nhắc nhở](https://user-images.githubusercontent.com/84552830/192385913-479db436-47ea-42fd-9b67-41d5281cdf73.gif) |
| [Widget cơ bản](https://github.com/S4ltF1sh/TodoApp#widget-c%C6%A1-b%E1%BA%A3n) | ![Widget Cơ bản](https://user-images.githubusercontent.com/84552830/192385968-15dec4bd-3ac2-48a7-9cb5-52823c4ca911.gif) |

# Chi tiết chức năng:
### Tạo mới Todo:
1. Nhấn vào FAB ở góc phải dưới màn hình để tạo mới Todo.

2. Nhấn vào ChipTime để chọn thời gian nhắc nhở.

![image](https://user-images.githubusercontent.com/84552830/192391308-6199414a-678c-41ce-a5e9-a94715e662fb.png)

3. Nhấn ChipGroup để chọn Group cho Todo:

![image](https://user-images.githubusercontent.com/84552830/192396541-a2ddf096-ca2b-4882-9510-430192f93536.png)

- Chọn các Group tương ứng để thay đổi Group cho Todo.

- Chọn Tạo nhóm mới để mở ra BottomSheet Tạo nhóm mới:

![image](https://user-images.githubusercontent.com/84552830/192391756-5199e0f4-3ebd-4ea0-93ff-57afadf2f888.png)

> Nút OK sẽ bị disable khi tên nhóm mới là 0 hoặc có độ dài > 100 ký tự.

![image](https://user-images.githubusercontent.com/84552830/192396622-35673a8b-e172-4eb2-9bd3-5020eddfad79.png)

4. Khi nhấn Back trên thanh điều hướng sẽ thêm Todo mới vào Database.

### Sửa Todo:
- Nhấn vào CardView Todo để vào xem và sửa Todo, các cập nhật sẽ được trigger khi nhấn Back trên thanh điều hướng.
- Khi Title và Not đều rỗng thì sẽ không cập nhật Todo (quay lại trạng thái vừa mở).

### Xoá và khôi phục Todo:
- Khi nhấn vào checkbox để hoàn thành hoặc xoá Todo đều sẽ đưa Todo vào thùng rác.
> Trên thực tế thì TodoStatus sẽ được chuyển đổi từ ON_GOING => DELETED or DONE (thùng rác sẽ load các Todo có TodoStatus là DELETED or DONE)

- Trong thùng rác có thể khôi phục hoặc xoá vĩnh viễn Todo.

![image](https://user-images.githubusercontent.com/84552830/192529266-216ccfb2-a516-46a0-bf24-0b2f8b7641dd.png)

- Khi khôi phục:
> Chuyển TodoStatus thành DELETED.
> Tạo nhắc nhở mới nếu có AlarmTime != null.
> Tạo Group mới nếu GroupName != "".

### Tìm kiếm Todo:
- Sử dụng @Query và LIKE kết + doAfterTextChange + handle().delay để search:
> Search bar thực tế là Edittext
> handle().delay để tránh doAfterTextChange check liên tục.

![image](https://user-images.githubusercontent.com/84552830/192393499-a602a365-2935-468f-aa56-4a506f3922b3.png)

![image](https://user-images.githubusercontent.com/84552830/192393835-2247c2cd-42dd-4c0d-a702-372275094543.png)

### Chia sẻ Todo:
- Sử dụng ImplictIntent với ACTION_SEND để gửi Title và Note đi.

![image](https://user-images.githubusercontent.com/84552830/192394181-6a4a0546-4fb9-46e7-b5c9-f2e26a5cc28b.png)

### Một số thao tác với Group:
- Group có thể đổi tên.

- Xoá Group: 
> Xoá Group ra khỏi Database.
> Chuyển toàn bộ TodoStatus của các Todo trong Group đó sang DELETED.
> Huỷ toàn bộ nhắc nhở.

- Nhấn giữ Todo và kéo thả vào Group để thêm Todo từ ngoài màn hình vào 1 Group đã tồn tại.
> Chuyển groupName thành Title của Group tương ứng.

### Multi Select:
- Có thể chọn nhiều Todo và Group 1 lúc để thực hiện các thao tác:

- Sử dụng 1 List để chứa các Item được chọn.

- Sử dụng 1 biến để lưu trạng thái khi nào tiến vào chế độ Multi Select được đặt ở trong ViewModel.

### Thêm và nhận nhắc nhở:
- Chỉ cần chọn thời gian nhắc nhở thì khi nhấn Back trên thanh điều hướng, ngay lập tức sẽ sử dụng [AlarmManager]() để thêm nhắc nhở.

- Thông báo đăng ký Channel với Important.HIGHT để hiện Headup trên màn hình.

- Sử dụng id của Todo để làm id cho PendingIntent khi tạo 1 sự kiện bằng AlarmManager, khi xoá cũng xoá theo id.

![image](https://user-images.githubusercontent.com/84552830/192395302-fa3a0ecd-0b62-44b9-a30b-51a83849d33b.png)

- Thông báo được custom khi mở rộng thông qua setCustomBigContentView().

- Khi nhấn vào thông báo sẽ nhảy thẳng vào TodoFragment để xem chi tiết Todo bằng DeepLink (sử dụng NavDeepLinkBuilder).

![image](https://user-images.githubusercontent.com/84552830/192395552-4c7b2b37-7351-4c62-8df6-b8c9416e37a8.png)

- Nếu nhấn vào "Đánh dấu là hoàn thành" sẽ gửi tín hiệu tới ActionFromNotification (BroadcastReceiver):
> Từ đây sẽ chuyển TodoStatus của Todo (xác định bằng id) thành DELETED.
> Gửi tín hiệu tới TodoWidget (AppWidgetProvider) để update Widget:

![image](https://user-images.githubusercontent.com/84552830/192396077-696a53a9-a45d-411e-abc6-16661279b974.png)

### Widget cơ bản:
- Widget bao gồm 5 phần:
> Phần tiêu đề Widget và dấu + (khi nhấn vào sẽ mở TodoFragment bằng cách dùng DeepLink cho phép tạo mới Todo)
> 4 phần còn lại là 4 Todo được mở gần nhất, có thể ấn vào để xem Todo tương tự dấu +.
> Todo nào đang có nhắc nhở thì sẽ có icon đồng hồ bên cạnh Title.

![image](https://user-images.githubusercontent.com/84552830/192530158-f5cce60b-bf93-4e1b-bca2-9f4147f2e4bd.png)

- Widget được update khi 1 Todo được update (xoá, checkdone, sửa, xem,...)


# Database:
1. Todo:

![image](https://user-images.githubusercontent.com/84552830/192390180-3abf0e3a-c38e-4c70-9749-677daaf5303a.png)

> Date được convert thành Long qua file Converter để Room lưu trữ được.

![image](https://user-images.githubusercontent.com/84552830/192390411-c89efdf8-8dfa-462b-87eb-5e6ddc9f12ad.png)

2. Group:

![image](https://user-images.githubusercontent.com/84552830/192390260-05d576bb-6003-433c-9d93-8338b6544b60.png)

3. GroupWithTodos:

- Sử dụng Relation 1:N để duyệt 2 bảng, trả về 1 Group với N Todo.
- Sử dụng @DatabaseView để thêm điều kiện vào GroupWithTodos.

![image](https://user-images.githubusercontent.com/84552830/192390520-8a730384-9618-422a-8f52-67dbdb526575.png)

# ViewModel:
- Thường sẽ chứa các LiveData chứa dữ liệu của các RecyclerView ở các Fragment.
> Khi LiveData thay đổi sẽ update lại RecyclerView tương ứng.

- Mỗi Fragment thường sẽ có 1 ViewModel tương ứng.

![image](https://user-images.githubusercontent.com/84552830/192530896-ba22bf1a-4201-4f46-b3fa-46985e49b97a.png)

- Riêng OnGoingFragment và GarbageFragment có các logic và dữ liệu chung và có thể update qua lại giữa các RecyclerView nên sẽ có HomeShareViewModel dùng chung
> 2 Fragment này là 2 Pager của HomeFragment.

![image](https://user-images.githubusercontent.com/84552830/192532614-66140e21-b086-457c-9900-332d2a91f122.png)

> Lưu ý là khi tạo ViewModel ở 2 Fragment kể trên cần chú ý phần ViewModelStoreOwner sẽ là requireParentFragment() và ở HomeFragment là _this_

![image](https://user-images.githubusercontent.com/84552830/192532705-2694e99e-c2d0-4cab-b94d-57ede734313d.png)
![image](https://user-images.githubusercontent.com/84552830/192532769-4d1685a5-bc0d-418d-9931-cf20b746598c.png)

> Làm vậy để từ HomeFragment hay 2 Fragment kia có thể update 2 Fragment còn lại khi thay đổi LiveData tương ứng (do có chung ViewModelStoreOwner là HomeFragment)

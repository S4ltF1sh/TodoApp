# Database:
1. Todo:

![image](https://user-images.githubusercontent.com/84552830/192390180-3abf0e3a-c38e-4c70-9749-677daaf5303a.png)

> Date được convert thành Long qua file Converter để Room lưu trữ được.

![image](https://user-images.githubusercontent.com/84552830/192390411-c89efdf8-8dfa-462b-87eb-5e6ddc9f12ad.png)

3. Group:

![image](https://user-images.githubusercontent.com/84552830/192390260-05d576bb-6003-433c-9d93-8338b6544b60.png)

4. GroupWithTodos:

- Sử dụng Relation 1:N để duyệt 2 bảng, trả về 1 Group với N Todo.
- Sử dụng @DatabaseView để thêm điều kiện vào GroupWithTodos.

![image](https://user-images.githubusercontent.com/84552830/192390520-8a730384-9618-422a-8f52-67dbdb526575.png)

# ViewModel:
# Chức năng:

| Chức năng | Mô tả |
| --- | --- |
| [Tạo mới Todo] | ![Tạo mới Todo](https://user-images.githubusercontent.com/84552830/192386174-1416b8f6-1cfe-4043-af0b-762f90ae2d4b.gif) |
| [Sửa Todo] | ![Sửa Todo](https://user-images.githubusercontent.com/84552830/192385410-c6a29545-e301-456e-b5a9-3b096450b12a.gif) |
| [Xoá và khôi phục Todo] | ![Xoá và khôi phục Todo](https://user-images.githubusercontent.com/84552830/192386217-e2682cd0-4b07-4e3c-88c5-9bd89cc01b75.gif) |
| [Tìm kiếm Todo] | ![Tìm kiếm Todo](https://user-images.githubusercontent.com/84552830/192385446-daad8d95-3a7e-4290-b20e-0a7b6fe567a7.gif) |
| [Chia sẻ Todo] | ![Chia sẻ Todo](https://user-images.githubusercontent.com/84552830/192385543-c282f12d-f6ef-4066-82bb-0655f0670699.gif) |
| [Một số thao tác với Group] | ![Một số thao tác với Group (2)](https://user-images.githubusercontent.com/84552830/192385591-5403eb2b-e4c6-4648-9c5f-fa448b64a0c0.gif) |
| [Multi Select] | ![Multi Select](https://user-images.githubusercontent.com/84552830/192385656-34b7a055-2ff9-4ee6-9461-f01536568848.gif) |
| [Thêm và nhận nhắc nhở] | ![Thêm nhắc nhở](https://user-images.githubusercontent.com/84552830/192385839-8dd4fe63-8ced-4b17-8bab-9b0541f5c792.gif) |
||![Nhận nhắc nhở](https://user-images.githubusercontent.com/84552830/192385913-479db436-47ea-42fd-9b67-41d5281cdf73.gif) |
| [Widget cơ bản] | ![Widget Cơ bản](https://user-images.githubusercontent.com/84552830/192385968-15dec4bd-3ac2-48a7-9cb5-52823c4ca911.gif) |

# Chi tiết chức năng:
## Tạo mới Todo:
1. Nhấn vào FAB ở góc phải dưới màn hình để tạo mới Todo.

3. Nhấn vào ChipTime để chọn thời gian nhắc nhở.

![image](https://user-images.githubusercontent.com/84552830/192391308-6199414a-678c-41ce-a5e9-a94715e662fb.png)

3. Nhấn ChipGroup để chọn Group cho Todo:

![image](https://user-images.githubusercontent.com/84552830/192396541-a2ddf096-ca2b-4882-9510-430192f93536.png)

- Chọn các Group tương ứng để thay đổi Group cho Todo.

- Chọn Tạo nhóm mới để mở ra BottomSheet Tạo nhóm mới:

![image](https://user-images.githubusercontent.com/84552830/192391756-5199e0f4-3ebd-4ea0-93ff-57afadf2f888.png)

> Nút OK sẽ bị disable khi tên nhóm mới là 0 hoặc có độ dài > 100 ký tự.

![image](https://user-images.githubusercontent.com/84552830/192396622-35673a8b-e172-4eb2-9bd3-5020eddfad79.png)

4. Khi nhấn Back trên thanh điều hướng sẽ thêm Todo mới vào Database.

## Sửa Todo:
## Xoá và khôi phục Todo:
- Khi nhấn vào checkbox để hoàn thành hoặc xoá Todo đều sẽ đưa Todo vào thùng rác.
> Trên thực tế thì TodoStatus sẽ được chuyển đổi từ ON_GOING => DELETED or DONE (thùng rác sẽ load các Todo có TodoStatus là DELETED or DONE)

- Trong thùng rác có thể khôi phục hoặc xoá vĩnh viễn Todo.

- Khi khôi phục:
> Chuyển TodoStatus thành DELETED.
> Tạo nhắc nhở mới nếu có AlarmTime != null.
> Tạo Group mới nếu GroupName != "".

## Tìm kiếm Todo:
- Sử dụng @Query và LIKE kết + doAfterTextChange + handle().delay để search:
> Search bar thực tế là Edittext
> handle().delay để tránh doAfterTextChange check liên tục.

![image](https://user-images.githubusercontent.com/84552830/192393499-a602a365-2935-468f-aa56-4a506f3922b3.png)

![image](https://user-images.githubusercontent.com/84552830/192393835-2247c2cd-42dd-4c0d-a702-372275094543.png)

## Chia sẻ Todo:
- Sử dụng ImplictIntent với ACTION_SEND để gửi Title và Note đi.

![image](https://user-images.githubusercontent.com/84552830/192394181-6a4a0546-4fb9-46e7-b5c9-f2e26a5cc28b.png)

## Một số thao tác với Group:
- Group có thể đổi tên.

- Xoá Group: 
> Xoá Group ra khỏi Database.
> Chuyển toàn bộ TodoStatus của các Todo trong Group đó sang DELETED.
> Huỷ toàn bộ nhắc nhở.

- Nhấn giữ Todo và kéo thả vào Group để thêm Todo từ ngoài màn hình vào 1 Group đã tồn tại.
> Chuyển groupName thành Title của Group tương ứng.

## Multi Select:
- Có thể chọn nhiều Todo và Group 1 lúc để thực hiện các thao tác:

## Thêm và nhận nhắc nhở:
- Chỉ cần chọn thời gian nhắc nhở thì khi nhấn Back trên thanh điều hướng, ngay lập tức sẽ sử dụng [AlarmManager]() để thêm nhắc nhở.

- Thông báo đăng ký Channel với Important.HIGHT để hiện Headup trên màn hình.

![image](https://user-images.githubusercontent.com/84552830/192395302-fa3a0ecd-0b62-44b9-a30b-51a83849d33b.png)

- Thông báo được custom khi mở rộng thông qua setCustomBigContentView().

- Khi nhấn vào thông báo sẽ nhảy thẳng vào TodoFragment để xem chi tiết Todo bằng DeepLink (sử dụng NavDeepLinkBuilder).

![image](https://user-images.githubusercontent.com/84552830/192395552-4c7b2b37-7351-4c62-8df6-b8c9416e37a8.png)

- Nếu nhấn vào "Đánh dấu là hoàn thành" sẽ gửi tín hiệu tới ActionFromNotification (BroadcastReceiver):
> Từ đây sẽ chuyển TodoStatus của Todo (xác định bằng id) thành DELETED.
> Gửi tín hiệu tới TodoWidget (AppWidgetProvider) để update Widget:

![image](https://user-images.githubusercontent.com/84552830/192396077-696a53a9-a45d-411e-abc6-16661279b974.png)

## Widget cơ bản:


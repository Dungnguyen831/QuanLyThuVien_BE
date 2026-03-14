# 📚 Hệ Thống Quản Lý Thư Viện (Library Management System - Backend)

Đây là mã nguồn Backend cho dự án Hệ Thống Quản Lý Thư Viện, cung cấp các RESTful API phục vụ cho việc quản lý sách, độc giả, quá trình mượn/trả và thống kê dữ liệu. Hệ thống được thiết kế theo kiến trúc phi tập trung (Decoupled Architecture), giao tiếp với Frontend thông qua chuỗi JSON.

## 🚀 Các tính năng chính (Features)

* **🔐 Xác thực & Phân quyền (Auth & Security):** Quản lý đăng nhập, đăng ký và phân quyền người dùng (Admin, Thủ thư, Độc giả) sử dụng Spring Security.
* **📖 Quản lý Sách & Kho:** Thêm, sửa, xóa sách, danh mục, tác giả, nhà xuất bản. Quản lý số lượng và trạng thái từng bản sao của sách (Book Copies).
* **👥 Quản lý Độc giả:** Quản lý thông tin tài khoản, lịch sử mượn trả và danh sách yêu thích (Wishlist) của người dùng.
* **🔄 Quản lý Mượn/Trả (Loans):** Tạo phiếu mượn mới, ghi nhận trả sách, tự động tính toán trạng thái (Đang mượn, Đã trả, Quá hạn).
* **📅 Quản lý Đặt trước (Reservations):** Cho phép độc giả đặt trước sách và quản lý danh sách chờ.
* **📊 Thống kê (Dashboard):** Cung cấp số liệu tổng quan về số sách, số người dùng, sách đang mượn và quá hạn.

## 🛠 Công nghệ sử dụng (Tech Stack)

* **Ngôn ngữ:** Java (JDK 25)
* **Framework:** Spring Boot 3.x
* **Database:** MySQL
* **ORM:** Spring Data JPA / Hibernate
* **Build Tool:** Maven
* **API Documentation:** Swagger / OpenAPI
* **Khác:** Lombok, Spring Security

## ⚙️ Hướng dẫn cài đặt & Chạy dự án (Local Setup)

### Yêu cầu môi trường (Prerequisites)
* Java Development Kit (JDK) 25
* MySQL Server (đang chạy ở port 3306)
* Maven (tùy chọn, dự án đã có sẵn Maven Wrapper)

### Các bước cài đặt
**Bước 1: Clone dự án về máy**
```bash
git clone [https://github.com/Dungnguyen831/QuanLyThuVien_BE.git](https://github.com/Dungnguyen831/QuanLyThuVien_BE.git)
cd QuanLyThuVien_BE

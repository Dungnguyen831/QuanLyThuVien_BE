    package com.library.server.dto.response;

    public class DashboardStatsDTO {
        // 1. Sửa lỗi thiếu kiểu dữ liệu và access modifier
        private long totalBooks;
        private long borrowedBooks;
        private long newReaders;
        private long overdueBooks;

        // 2. Thêm Constructor không tham số (Bắt buộc cho một số thư viện mapping)
        public DashboardStatsDTO() {
        }

        // 3. Thêm Constructor có tham số (Để ông dùng trong DashboardService)
        public DashboardStatsDTO(long totalBooks, long borrowedBooks, long newReaders, long overdueBooks) {
            this.totalBooks = totalBooks;
            this.borrowedBooks = borrowedBooks;
            this.newReaders = newReaders;
            this.overdueBooks = overdueBooks;
        }

        // 4. Getter và Setter (Để Frontend có thể đọc được dữ liệu qua API)
        public long getTotalBooks() {
            return totalBooks;
        }

        public void setTotalBooks(long totalBooks) {
            this.totalBooks = totalBooks;
        }

        public long getBorrowedBooks() {
            return borrowedBooks;
        }

        public void setBorrowedBooks(long borrowedBooks) {
            this.borrowedBooks = borrowedBooks;
        }

        public long getNewReaders() {
            return newReaders;
        }

        public void setNewReaders(long newReaders) {
            this.newReaders = newReaders;
        }

        public long getOverdueBooks() {
            return overdueBooks;
        }

        public void setOverdueBooks(long overdueBooks) {
            this.overdueBooks = overdueBooks;
        }
    }
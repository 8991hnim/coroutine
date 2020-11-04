# Demo project ở branch master

# Giới thiệu

Coroutine giống như light-weight thread. Nhưng nó không phải là thread. Nó giống thread ở chỗ là các coroutine có thể chạy song song, đợi nhau và trao đổi dữ liệu với nhau. Sự khác biệt lớn nhất so với thread là coroutine rất rẻ, gần như là hàng free, chúng ta có thể chạy hàng nghìn coroutine mà gần như không ảnh hưởng lớn đến performance.<br>
Một thread có thể chạy nhiều coroutine.

# Implementation

```kotlin
def coroutine_version = "lastest_version"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutine_version"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutine_version"
```

# Suspend Function
__suspend function__ có khả năng ngừng hay gián đoạn việc thực thi một lát (trạng thái ngừng là trạng thái suspend) và có thể tiếp tục thực thi lại khi cần thiết<br>
* Suspend function được đánh dấu bằng từ từ khóa suspend
```kotlin
suspend fun helloWorld() {
    delay(1000)
    println("Hello World!")
}
```
* Chỉ có thể được gọi suspend function bên trong một suspend function khác hoặc bên trong một coroutine

# Coroutine Context
__Coroutine Context__ bao gồm:
* Coroutine Dispatcher<br>
__IO__ : chạy trên background thread. Thường được dùng khi Read, write files, Database, Networking<br>
__Main__: chạy trên main UI thread<br>
__Default__: chạy trên background thread. Thường được dùng khi làm các công việc nặng như sorting a list, parse Json.<br>
* Coroutine Exception Handler : bắt lỗi trong coroutine
* Coroutine name : đặt tên cho coroutine, dùng để debug (không quan trọng)
* Job : giữ nhiệm vụ nắm giữ thông tin về lifecycle của coroutine, cancel coroutine

__Tạo 1 coroutine đơn giản__
```kotlin
  CoroutineScope([CoroutineContext]).launch {
    delay(500) // giống với Handler.postDelayed(500)
    Log.d(TAG, "Coroutine is easy ${Thread.currentThread().name}")
  }
```

## Chuyển đổi CoroutineDispatcher trong cùng 1 coroutine
__withContext([CoroutineDispatcher])__: Sử dụng để chuyển đổi coroutine context trong cùng 1 coroutine<br>
Ví dụ có thể chuyển đổi luồng từ background sang main thread 1 cách đơn giản như sau
```kotlin
  CoroutineScope([CoroutineContext]).launch {
    Log.d(TAG, "Coroutine is easy ${Thread.currentThread().name}")
    withContext(Main){
      Log.d(TAG, "I'm in main thread ${Thread.currentThread().name}")
    }
  }
```

## Job 
__Job__ Job giữ nhiệm vụ nắm giữ thông tin về lifecycle của coroutine, cancel coroutine
### Cancellation
```kotlin
  val jobA = Job()
  CoroutineScope([CoroutineContext] + jobA).launch {
    //your code
  }
  
  jobA.cancel() //hủy bỏ tất cả các coroutine đc quản lý bởi jobA
```
__Lưu ý__ 1 khi job đã bị cancel không thể quản lý 1 coroutine nào khác nữa, phải khởi tạo lại job để sử dụng tiếp

### Làm gì đó khi job thực thi xong
```kotlin
  val job = CoroutineScope([CoroutineContext] + jobA).launch {
    //your code
  }
  
  job.invokeOnCompletion { throwable ->
      //coroutine đã chạy xong (có thể đã hoàn thành/bị cancel/có lỗi làm coroutine bị dừng)
  }
 
```

# Flow
__Khối flow { }__ là một builder function giúp ta tạo ra 1 đối tượng Flow<br>
__emit__ dùng để emit các giá trị từ Flow<br>
__flow.collect{}__ sử dụng để nhận các giá trị được trả về từ flow thông qua emit<br>
```kotlin
val mFlow = flow<Int> {
    for(i in 0 until 10){
        emit(i) 
        delay(1000)
    }
}

mFlow.collect {
  Log.d(TAG, "Got result from mFlow $it") //it sẽ nhận giá trị trả về là 0 -> 9
}
```
__Flow là cold streams__ Điều đó có nghĩa là code bên trong flow { } sẽ không chạy cho đến khi Flow gọi hàm collect.<br>
__Flow có thể bị hủy khi scope chứa flow bị hủy__<br>

# Kết hợp coroutine với room và retrofit
Ví dụ
```kotlin
    @GET("rented/getTopBorrowedBook")
    suspend fun getTopBorrowedBook(): ApiWrapper<List<TopBookNetworkEntity>>
```

```kotlin
    @Insert(onConflict = IGNORE)
    suspend fun addBookCategory(bookCategoryCacheEntity: BookCategoryCacheEntity)
```

# Bài toán demo
* Xử lí Search View
* Đa luồng api

# Tìm hiểu thêm
* Handle exception với coroutine
* Các toán tử của flow
* Channels
<br>https://www.youtube.com/watch?v=bM7PVVL_5GM
<br>https://viblo.asia/p/cung-hoc-kotlin-coroutine-phan-1-gioi-thieu-kotlin-coroutine-va-ky-thuat-lap-trinh-bat-dong-bo-gGJ59xajlX2

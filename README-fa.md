<div align="center">

# ⚡ VegaNG

**یک کلاینت VPN سریع و سبک برای اندروید**

وی‌گا‌ان‌جی یک اپلیکیشن VPN مدرن و پرسرعت است که از هسته V2Ray برای ارتباطی سریع و مطمئن استفاده می‌کند.

[![Android](https://img.shields.io/badge/Android-8.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange?style=for-the-badge)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

[**English**](README.md)

</div>

---

## ✨ امکانات

| ⚡ عملکرد | 🎨 طراحی |
|:---------------|:----------|
| 🕐 تست پینگ تکی و گروهی | 🎯 متریال ۳ با جت‌پک کامپوز |
| 📊 مرتب‌سازی خودکار بر اساس کمترین پینگ | 🌍 دوزبانه فارسی و انگلیسی (RTL) |
| 🗑️ پاکسازی یک‌کلیکی کانفیگ‌های ازکارافتاده | 🚀 طراحی تمیز برای حالت خالی |
| 🔒 هسته پایدار و امن V2Ray | 🌓 پشتیبانی از تم روشن و تیره |

---

## 🖼️ اسکرین‌شات‌ها

<p align="center">
  <img src="screenshots/home_light.jpg" width="150" title="صفحه اصلی روشن">
  <img src="screenshots/home_dark.jpg" width="150" title="صفحه اصلی تیره">
  <img src="screenshots/configs_light.jpg" width="150" title="لیست کانفیگ‌ها روشن">
  <img src="screenshots/configs_dark.jpg" width="150" title="لیست کانفیگ‌ها تیره">
  <img src="screenshots/connected_light.jpg" width="150" title="وضعیت اتصال روشن">
  <img src="screenshots/connected_dark.jpg" width="150" title="وضعیت اتصال تیره">
</p>

---

## 📥 نصب

آخرین نسخه APK را از [Releases](https://github.com/KianMahmoudi/VegaNG/releases) دانلود کنید.

> **نکته:** از آنجا که برنامه در گوگل‌پلی منتشر نشده، اندروید هشدار «نصب از منابع ناشناس» نمایش می‌دهد. کافی است برای این نصب آن را مجاز کنید.

---

## 🚀 شروع سریع

```
1. Get   ← دریافت کانفیگ‌ها از سرور
2. Test  ← اندازه‌گیری پینگ همه کانفیگ‌ها
3. Sort  ← سریع‌ترین سرورها در بالا
4. Clean ← حذف کانفیگ‌های ازکارافتاده
5. کلیک روی یک کانفیگ ← اتصال
```

---

## 🛠️ ساخت از سورس

**پیش‌نیازها:** اندروید استودیو · JDK 17+ · Android SDK 35

```bash
git clone https://github.com/KianMahmoudi/VegaNG.git
./gradlew assembleDebug
./gradlew assembleRelease
```

خروجی APK:

```
app/build/outputs/apk/{debug,release}/app-{debug,release}.apk
```

---

## 🏗️ معماری

ساخته‌شده با معماری تمیز **MVVM**:

```
┌───────────────────────────────────┐
│            لایه رابط کاربری       │
│   Jetpack Compose · Material 3    │
├───────────────────────────────────┤
│          ViewModel (Hilt)         │
│   state · events · coroutines     │
├───────────────────────────────────┤
│           Repository              │
│   ConfigRepository · VpnRepository │
├───────────────────────────────────┤
│            لایه داده              │
│   Room DB · Config Parser         │
├───────────────────────────────────┤
│            هسته VPN               │
│      libv2ray · tun2socks         │
└───────────────────────────────────┘
```

---

## تکنولوژی‌ها

- **زبان:** کاتلین
- **رابط کاربری:** جت‌پک کامپوز + متریال ۳
- **معماری:** MVVM + الگوی Repository
- **تزریق وابستگی:** Hilt
- **دیتابیس:** Room
- **هسته VPN:** libv2ray + tun2socks
- **آسنکرون:** Coroutines + Flow
# SPECTRA — AR Information Layer

**A Kotlin / Jetpack Compose Augmented Reality Intelligence Platform for Android.**

![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?logo=kotlin&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Android%2026%2B-3DDC84?logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)
![ARCore](https://img.shields.io/badge/AR-ARCore-000000)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

> 🇬🇧 [English](#-english) &nbsp;·&nbsp; 🇮🇷 [فارسی](#-فارسی) &nbsp;·&nbsp; 🇨🇳 [中文](#-中文)

---

## 🇬🇧 English

### Overview

**SPECTRA** turns a phone's camera into a live, interactive information layer. Point the
camera at the world and SPECTRA combines **Computer Vision**, **Augmented Reality**,
**OCR**, **Object Detection**, **Location**, **Maps**, and **Personal Context** to place
relevant information directly on top of the camera feed — turning the physical world into
an interface you can act on.

The project is written end-to-end in **Kotlin**, built on **Jetpack Compose + Material 3**,
and designed around **Clean Architecture (MVVM/MVI + Repository pattern)** with **Hilt**,
**Coroutines/Flow**, **CameraX**, **ARCore**, **ML Kit**, and pluggable **TensorFlow Lite /
ONNX Runtime Mobile** backends for custom on-device models.

### ✨ Key Features

- **AR Camera Engine** — real-time CameraX pipeline with a backpressure-aware analyzer that
  adapts detection frequency to device power/thermal state.
- **Object Recognition** — ML Kit Object Detection & Tracking by default, with pluggable
  custom **TFLite** / **ONNX** model backends per Lens.
- **Real-time OCR** — ML Kit Text Recognition with tap-to-act text (Copy, Translate,
  Search, Save, Summarize, Add to Notes, Create Reminder, Open Link, Share).
- **Spatial Anchors & Personal AR Memory** — pin notes, tasks, and reminders to real-world
  locations using ARCore anchors and plane detection; they resurface when you're back.
- **User-Entered Opening Hours** — a fully on-device, fully user-driven opening-hours
  engine: *you* type in the hours for any place or personal marker, and SPECTRA computes
  **open/closed status and a live countdown to the next change** locally — no external
  business-hours API involved, anywhere.
- **AR Navigation** — turn-by-turn AR arrows and waypoints from Fused Location + a
  pluggable `MapProvider` (Google Maps by default; MapLibre-ready `foss` flavor).
- **AI Vision Assistant** — ask questions about what the camera sees. Fully **on-device by
  default**; an optional Cloud AI provider only ever runs after explicit, per-request
  confirmation.
- **Lens System** — Explore, Text, Object, Travel, Memory, Shopping, Study, Navigation, and
  a **Lens Builder** for fully custom lenses.
- **Privacy Center & Data Retention** — every permission is explained in plain language,
  requested only when needed, and revocable at any time; configurable auto-delete windows
  per data category.
- **Security** — local database encrypted at rest with SQLCipher, keys sealed in the
  Android Keystore, optional biometric lock for personal AR data.
- **Offline-First** — camera, OCR, object detection, personal markers, notes, and local
  search all work with no network connection.
- **Windows 11–Inspired Theming** — Light, Dark, and System modes, each with a **Windows
  Default**, **Blue**, or **Red** accent (Fluent-style Mica surfaces, 8dp rounding,
  optional AMOLED true-black and Material You dynamic color).
- **Full Localization** — English and Chinese (LTR) and Persian (true RTL), switchable
  in-app independent of system locale.

### 🏗️ Architecture

```
Presentation   → Jetpack Compose + Material 3, MVVM ViewModels, Navigation-Compose
Domain/Core    → Use-case-level orchestration inside each feature module
Data           → Room (SQLCipher-encrypted) + DataStore Preferences + Repositories
Vision Layer   → CameraX → FrameAnalyzer → ObjectDetector / OcrEngine → ObjectTracker
AR Layer       → ARCore Session/Anchor/Plane management → 2D screen-space overlay renderer
DI             → Hilt across every layer
Background     → WorkManager (model downloads, cleanup, indexing, backups, reminders)
```

Each domain — `core`, `camera`, `ar`, `vision`, `ocr`, `tracking`, `spatial`, `location`,
`maps`, `ai`, `search`, `memory`, `tasks`, `notifications`, `models`, `data`, `security`,
`privacy`, `workers`, `analytics`, `power`, `openinghours`, `voice`, `backup`, and `ui` —
is a self-contained Kotlin package under `app/src/main/java/com/spectra/ar/`.

### 🧰 Tech Stack

| Layer              | Technology                                                   |
|--------------------|---------------------------------------------------------------|
| Language           | Kotlin                                                        |
| UI                 | Jetpack Compose, Material 3, Navigation-Compose                |
| AR                 | ARCore                                                        |
| Camera             | CameraX (Preview, ImageAnalysis, ImageCapture, VideoCapture)   |
| OCR / Vision       | Google ML Kit (Text Recognition, Object Detection, Translate, Language ID) |
| Custom models      | TensorFlow Lite, ONNX Runtime Mobile                           |
| DI                 | Hilt                                                           |
| Async              | Kotlin Coroutines + Flow                                       |
| Persistence        | Room + SQLCipher, SQLite FTS4 (search), DataStore Preferences  |
| Background work    | WorkManager                                                    |
| Location / Maps    | Fused Location Provider, Google Maps SDK (Compose) / MapLibre  |
| Security           | Android Keystore, EncryptedSharedPreferences, BiometricPrompt  |
| Networking (opt-in)| Retrofit, OkHttp, kotlinx.serialization                        |
| Testing            | JUnit4, MockK, Truth, Turbine, Robolectric, Compose UI Test, Room Testing, WorkManager Testing |

### 📁 Project Structure

```
SPECTRA/
├── app/
│   ├── build.gradle.kts
│   ├── src/main/java/com/spectra/ar/
│   │   ├── core/            → DI modules, dispatchers, constants, permissions
│   │   ├── ui/               → Compose theme, navigation, screens, components
│   │   ├── camera/           → CameraX controller + backpressure-aware analyzer
│   │   ├── ar/                → ARCore session, anchors, planes, occlusion, pose math
│   │   ├── vision/            → Object detection (ML Kit / TFLite / ONNX)
│   │   ├── ocr/                → Text recognition, language ID, translation
│   │   ├── tracking/          → Multi-frame IOU object tracker
│   │   ├── spatial/           → Spatial anchors repository, Spatial Journal
│   │   ├── location/          → Fused Location, Places
│   │   ├── maps/              → Map provider abstraction, AR navigation engine
│   │   ├── ai/                 → AI Vision Assistant (local-first, cloud opt-in)
│   │   ├── search/            → Local full-text search (FTS4)
│   │   ├── memory/, tasks/    → Personal AR notes & tasks
│   │   ├── notifications/     → Spatial reminders
│   │   ├── models/             → Custom model download/verify/manage
│   │   ├── data/                → Room database, DAOs, entities, preferences
│   │   ├── security/           → Keystore, encryption, biometric lock
│   │   ├── privacy/            → Permission Center, data retention policy
│   │   ├── workers/            → WorkManager background jobs
│   │   ├── analytics/          → Local-only session analytics
│   │   ├── power/               → Battery/thermal-aware quality scaling
│   │   ├── openinghours/       → User-entered opening-hours engine
│   │   ├── voice/               → Optional hands-free voice commands
│   │   └── backup/              → Encrypted export/import
│   └── src/{test,androidTest}/  → Unit + instrumented tests
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── local.properties.example
```

### ✅ Prerequisites

- **Android Studio** — Koala (2024.1) or newer
- **JDK 17**
- **Android SDK Platform 34**, plus a device or emulator running **API 26+**
- An **ARCore-supported device** for full AR functionality (SPECTRA falls back to a
  camera-only mode on unsupported devices)
- A **Google Maps SDK API key** (only required for the Map / AR Navigation Lens — every
  other feature works without it)

### ⚙️ Installation & Setup

1. **Open the project.** Extract this archive and open the `SPECTRA/` folder as a project
   in Android Studio (**File → Open**).
2. **Configure local secrets.** Copy `local.properties.example` to `local.properties` in
   the project root and fill in:
   ```properties
   sdk.dir=/path/to/your/Android/sdk
   MAPS_API_KEY=your_google_maps_api_key
   CLOUD_AI_API_KEY=
   ```
   `MAPS_API_KEY` is only needed for the Map/Navigation Lens. `CLOUD_AI_API_KEY` can be
   left blank — Cloud AI is opt-in and disabled by default.
3. **Let Gradle sync.** Android Studio will prompt to sync automatically; otherwise use
   **File → Sync Project with Gradle Files**.
4. **Select a product flavor.** Use the `standard` flavor for the full Google Play
   Services build, or `foss` for a build without Google Maps/Play Services.
5. **Choose a run configuration.** Pick `standardDebug` (or `fossDebug`) from the build
   variants panel.
6. **Run.** Connect an ARCore-supported device (or a compatible emulator image) and press
   **Run ▶**.

### 🔐 Permissions

Every permission is requested contextually — the first time the feature that needs it is
used — and explained in the in-app **Privacy Center** (Settings → Privacy):

| Permission        | Required? | Used for                                             |
|--------------------|-----------|--------------------------------------------------------|
| Camera             | Core      | Every AR / vision feature                              |
| Location           | Optional  | Nearby places, AR navigation, spatial memories          |
| Microphone         | Optional  | Hands-free voice commands to the AI Vision Assistant     |
| Photos & media     | Optional  | Saving captures, importing photos into the Spatial Journal |
| Notifications      | Optional  | Spatial reminders and task due dates                     |

### 🧪 Testing

```bash
# Unit tests (JVM, no device required)
./gradlew testStandardDebugUnitTest

# Instrumented tests (requires a connected device/emulator)
./gradlew connectedStandardDebugAndroidTest
```

Unit tests cover the opening-hours calculator (overnight ranges, multiple daily ranges,
closing/opening-soon thresholds, unconfigured/all-closed edge cases), the object tracker,
and repository behavior. Instrumented tests cover the Room schema, WorkManager
integration, Compose UI, and documented runtime scenarios (camera/location permission
denial, unsupported AR devices, tracking loss, low battery, thermal throttling, offline
mode, missing models, low-confidence filtering, background/foreground transitions, device
rotation, and process death).

### 🌍 Localization

SPECTRA ships with complete **English**, **Persian (فارسی)**, and **Chinese (中文)**
translations. Persian renders as true right-to-left; English and Chinese render
left-to-right. Language can be changed from Settings independently of the device's system
language.

### 📄 License

Distributed under the **MIT License**. See the `LICENSE` file you add to your repository
for full terms.

---

## 🇮🇷 فارسی

### معرفی

**اسپکترا** دوربین گوشی را به یک لایه اطلاعاتی زنده و تعاملی تبدیل می‌کند. دوربین را به سمت
جهان بگیرید؛ اسپکترا با ترکیب **بینایی ماشین**، **واقعیت افزوده**، **تشخیص متن (OCR)**،
**تشخیص اشیاء**، **موقعیت مکانی**، **نقشه** و **زمینه شخصی کاربر**، اطلاعات مرتبط را
مستقیماً روی تصویر دوربین قرار می‌دهد — و به این ترتیب جهان فیزیکی به رابطی قابل تعامل
تبدیل می‌شود.

این پروژه به‌طور کامل با **Kotlin** نوشته شده، رابط کاربری آن با **Jetpack Compose و
Material 3** ساخته شده، و معماری آن بر پایه **Clean Architecture (الگوی MVVM/MVI +
Repository)** با **Hilt**، **Coroutines/Flow**، **CameraX**، **ARCore**، **ML Kit** و
بک‌اندهای مدل قابل‌جایگزین **TensorFlow Lite / ONNX Runtime Mobile** برای مدل‌های سفارشی
روی دستگاه طراحی شده است.

### ✨ ویژگی‌های کلیدی

- **موتور دوربین واقعیت افزوده** — پایپ‌لاین بلادرنگ CameraX با Analyzer آگاه از فشار
  پردازشی که فرکانس تشخیص را متناسب با توان و دمای دستگاه تنظیم می‌کند.
- **تشخیص اشیاء** — به‌صورت پیش‌فرض با ML Kit، به‌همراه امکان استفاده از مدل‌های سفارشی
  **TFLite** یا **ONNX** برای هر لنز.
- **تشخیص متن بلادرنگ** — با ML Kit، به‌همراه کنش‌های لمسی روی متن (کپی، ترجمه، جستجو،
  ذخیره، خلاصه‌سازی، افزودن به یادداشت، ساخت یادآور، باز کردن لینک، اشتراک‌گذاری).
- **لنگرهای فضایی و حافظه شخصی واقعیت افزوده** — سنجاق کردن یادداشت، وظیفه و یادآور به
  مکان‌های واقعی با استفاده از Anchor و Plane Detection در ARCore؛ با بازگشت به همان
  مکان، همان اطلاعات دوباره نمایش داده می‌شود.
- **ساعات کاری وارد‌شده توسط کاربر** — یک موتور ساعات کاری کاملاً روی دستگاه و کاملاً
  متکی بر ورودی کاربر: **خود شما** ساعات هر مکان یا نشانگر شخصی را وارد می‌کنید، و اسپکترا
  وضعیت **باز/بسته و شمارش معکوس زنده تا زمان تغییر بعدی** را به‌طور محلی محاسبه می‌کند —
  بدون هیچ سرویس بیرونیِ ساعات کاری در هیچ بخشی از برنامه.
- **مسیریابی واقعیت افزوده** — فلش‌ها و نقاط مسیر واقعیت‌افزوده گام‌به‌گام از Fused
  Location به‌همراه یک `MapProvider` قابل‌جایگزین (پیش‌فرض Google Maps؛ نسخه `foss` آماده
  برای MapLibre).
- **دستیار هوشمند بینایی** — درباره چیزی که دوربین می‌بیند سؤال بپرسید. به‌طور پیش‌فرض
  کاملاً **روی دستگاه**؛ سرویس اختیاری هوش مصنوعی ابری فقط پس از تأیید صریح کاربر برای هر
  درخواست اجرا می‌شود.
- **سامانه لنزها** — کاوش، متن، اشیاء، سفر، خاطرات، خرید، مطالعه، مسیریابی و یک **سازنده
  لنز** برای ساخت لنزهای کاملاً سفارشی.
- **مرکز حریم خصوصی و نگهداری داده** — هر دسترسی به زبان ساده توضیح داده می‌شود، فقط در
  زمان نیاز درخواست می‌شود و هر زمان قابل لغو است؛ بازه‌های حذف خودکار برای هر دسته داده
  قابل تنظیم است.
- **امنیت** — رمزنگاری پایگاه‌داده محلی با SQLCipher، نگهداری کلیدها در Android Keystore،
  قفل بیومتریک اختیاری برای داده‌های شخصی واقعیت افزوده.
- **اولویت با حالت آفلاین** — دوربین، تشخیص متن، تشخیص اشیاء، نشانگرهای شخصی، یادداشت‌ها
  و جستجوی محلی همگی بدون اتصال به اینترنت کار می‌کنند.
- **ظاهر الهام‌گرفته از ویندوز ۱۱** — حالت‌های روشن، تاریک و سیستم، هرکدام با رنگ تأکیدی
  **پیش‌فرض ویندوز**، **آبی** یا **قرمز** (سطوح شیشه‌ای به سبک Fluent، گردی ۸dp، حالت
  اختیاری مشکی کامل AMOLED و رنگ پویای Material You).
- **بومی‌سازی کامل** — انگلیسی و چینی (چپ‌به‌راست) و فارسی (راست‌به‌چپ واقعی)، قابل تغییر
  از داخل برنامه و مستقل از زبان سیستم.

### ✅ پیش‌نیازها

- **Android Studio** نسخه Koala (۲۰۲۴.۱) یا جدیدتر
- **JDK 17**
- **Android SDK Platform 34**، به‌همراه دستگاه یا شبیه‌ساز با **API 26+**
- یک **دستگاه دارای پشتیبانی ARCore** برای عملکرد کامل واقعیت افزوده (در دستگاه‌های بدون
  پشتیبانی، اسپکترا به حالت فقط‌دوربین بازمی‌گردد)
- یک **کلید API از Google Maps SDK** (فقط برای لنز نقشه/مسیریابی لازم است — سایر
  قابلیت‌ها بدون آن هم کار می‌کنند)

### ⚙️ نصب و راه‌اندازی

۱. **باز کردن پروژه.** فایل فشرده را استخراج کرده و پوشه `SPECTRA/` را به‌عنوان یک پروژه
   در Android Studio باز کنید (**File → Open**).

۲. **تنظیم کلیدهای محلی.** فایل `local.properties.example` را در ریشه پروژه به
   `local.properties` کپی کرده و مقادیر زیر را تکمیل کنید:
   ```properties
   sdk.dir=/path/to/your/Android/sdk
   MAPS_API_KEY=your_google_maps_api_key
   CLOUD_AI_API_KEY=
   ```
   `MAPS_API_KEY` فقط برای لنز نقشه/مسیریابی لازم است. `CLOUD_AI_API_KEY` را می‌توانید
   خالی بگذارید — هوش مصنوعی ابری اختیاری و به‌صورت پیش‌فرض غیرفعال است.

۳. **همگام‌سازی Gradle.** Android Studio به‌طور خودکار درخواست همگام‌سازی می‌دهد؛ در غیر
   این صورت از مسیر **File → Sync Project with Gradle Files** استفاده کنید.

۴. **انتخاب Product Flavor.** از flavor با نام `standard` برای نسخه کامل با Google Play
   Services، یا `foss` برای نسخه بدون Google Maps/Play Services استفاده کنید.

۵. **انتخاب Run Configuration.** از پنل Build Variants گزینه `standardDebug` (یا
   `fossDebug`) را انتخاب کنید.

۶. **اجرا.** یک دستگاه دارای پشتیبانی ARCore (یا شبیه‌ساز سازگار) را متصل کرده و دکمه
   **Run ▶** را بزنید.

### 🔐 دسترسی‌ها

هر دسترسی به‌صورت زمینه‌محور — یعنی فقط در اولین باری که قابلیت مربوطه استفاده می‌شود —
درخواست می‌شود و در **مرکز حریم خصوصی** داخل برنامه (تنظیمات ← حریم خصوصی) توضیح داده
می‌شود:

| دسترسی | الزامی است؟ | کاربرد |
|---|---|---|
| دوربین | اصلی | تمام قابلیت‌های واقعیت افزوده و بینایی ماشین |
| موقعیت مکانی | اختیاری | مکان‌های نزدیک، مسیریابی واقعیت افزوده، خاطرات فضایی |
| میکروفون | اختیاری | دستورهای صوتی دست‌آزاد به دستیار هوشمند بینایی |
| عکس‌ها و رسانه | اختیاری | ذخیره تصاویر گرفته‌شده، وارد کردن عکس به دفترچه فضایی |
| اعلان‌ها | اختیاری | یادآورهای فضایی و سررسید وظایف |

### 🧪 تست

```bash
# تست‌های واحد (روی JVM، بدون نیاز به دستگاه)
./gradlew testStandardDebugUnitTest

# تست‌های ابزارمند (نیازمند دستگاه یا شبیه‌ساز متصل)
./gradlew connectedStandardDebugAndroidTest
```

تست‌های واحد، محاسبه‌گر ساعات کاری (بازه‌های شبانه، چند بازه در یک روز، آستانه‌های
نزدیک‌به‌باز/نزدیک‌به‌بسته، حالت‌های مرزی تنظیم‌نشده/تمام‌هفته‌بسته)، ردیاب اشیاء و
رفتار مخازن داده را پوشش می‌دهند. تست‌های ابزارمند نیز طرحواره Room، یکپارچگی
WorkManager، رابط کاربری Compose و سناریوهای مستندشده زمان اجرا (رد دسترسی دوربین/موقعیت
مکانی، دستگاه‌های بدون پشتیبانی واقعیت افزوده، از‌دست‌رفتن ردیابی، باتری کم، افت حرارتی،
حالت آفلاین، مدل گمشده، فیلتر اطمینان پایین، انتقال پس‌زمینه/پیش‌زمینه، چرخش دستگاه و
مرگ فرآیند) را پوشش می‌دهند.

### 🌍 بومی‌سازی

اسپکترا با ترجمه کامل **انگلیسی**، **فارسی** و **چینی** عرضه می‌شود. فارسی به‌صورت
راست‌به‌چپ واقعی و انگلیسی و چینی به‌صورت چپ‌به‌راست نمایش داده می‌شوند. زبان برنامه از
داخل تنظیمات و مستقل از زبان سیستم‌عامل قابل تغییر است.

### 📄 مجوز

این پروژه تحت **مجوز MIT** منتشر می‌شود. برای شرایط کامل، فایل `LICENSE` را به مخزن خود
اضافه کنید.

---

## 🇨🇳 中文

### 项目概述

**SPECTRA** 将手机摄像头变成一层实时、可交互的信息层。将镜头对准周围的世界,SPECTRA 会结
合 **计算机视觉**、**增强现实**、**文字识别(OCR)**、**物体检测**、**位置信息**、**地图**
以及 **个人上下文**,把相关信息直接叠加在摄像头画面之上——让物理世界本身变成一个可以操作
的界面。

整个项目完全使用 **Kotlin** 编写,界面基于 **Jetpack Compose 与 Material 3** 构建,架构
采用 **Clean Architecture(MVVM/MVI + Repository 模式)**,并结合 **Hilt**、
**Coroutines/Flow**、**CameraX**、**ARCore**、**ML Kit**,以及可插拔的 **TensorFlow Lite
/ ONNX Runtime Mobile** 后端以支持设备端自定义模型。

### ✨ 核心功能

- **AR 相机引擎** — 实时 CameraX 处理管线,内置感知背压的分析器,可根据设备电量与温度状
  态自动调整检测频率。
- **物体识别** — 默认使用 ML Kit 物体检测与跟踪,并支持为每个镜头模式插入自定义的
  **TFLite** 或 **ONNX** 模型后端。
- **实时文字识别** — 基于 ML Kit,支持点击文字后执行复制、翻译、搜索、保存、生成摘要、添
  加到笔记、创建提醒、打开链接、分享等操作。
- **空间锚点与个人 AR 记忆层** — 利用 ARCore 的锚点与平面检测,将笔记、任务和提醒固定在真
  实世界的位置上;当你再次回到该位置时,这些信息会重新出现。
- **用户自主输入的营业时间** — 一套完全在设备本地运行、完全由用户驱动的营业时间引擎:
  **由你自己**为任意地点或个人标记输入时间,SPECTRA 会在本地计算出**营业/打烊状态,以及
  距离下次状态变化的实时倒计时**——整个应用中不涉及任何外部营业时间数据源。
- **AR 导航** — 基于 Fused Location 与可替换的 `MapProvider`(默认 Google Maps,`foss`
  版本已预留 MapLibre 支持)提供逐步指引的 AR 箭头与路径点。
- **AI 视觉助手** — 就摄像头看到的内容提问。默认**完全在设备本地**运行;可选的云端 AI 服
  务仅在每次请求都获得用户明确同意后才会被调用。
- **镜头系统** — 探索、文字、物体、旅行、回忆、购物、学习、导航,以及一个用于创建完全自
  定义镜头的 **镜头构建器**。
- **隐私中心与数据保留** — 每项权限都以通俗语言说明用途,仅在真正需要时才会请求,并可随
  时撤销;每类数据的自动删除周期均可自行配置。
- **安全性** — 本地数据库使用 SQLCipher 静态加密,密钥保存在 Android Keystore 中,个人
  AR 数据可选生物识别锁定。
- **离线优先** — 相机、文字识别、物体检测、个人标记、笔记与本地搜索均可在完全无网络的情
  况下正常使用。
- **Windows 11 风格主题** — 浅色、深色与跟随系统三种模式,每种模式均可搭配 **Windows 默
  认**、**蓝色** 或 **红色** 强调色(Fluent 风格的亚克力质感表面、8dp 圆角、可选纯黑
  AMOLED 模式与 Material You 动态取色)。
- **完整本地化** — 提供英文、中文(从左至右)与波斯语(真正的从右至左)三种语言,可在应
  用内独立于系统语言进行切换。

### ✅ 环境要求

- **Android Studio** — Koala(2024.1)或更高版本
- **JDK 17**
- **Android SDK Platform 34**,以及一台运行 **API 26 及以上**的设备或模拟器
- 若需完整 AR 功能,需要一台 **支持 ARCore 的设备**(不支持的设备上 SPECTRA 会自动回退到
  纯相机模式)
- 一个 **Google Maps SDK API 密钥**(仅地图/AR 导航镜头需要——其余所有功能均无需该密钥)

### ⚙️ 安装与配置

1. **打开项目。** 解压此压缩包后,在 Android Studio 中通过 **File → Open** 打开
   `SPECTRA/` 文件夹作为项目。
2. **配置本地密钥。** 将项目根目录下的 `local.properties.example` 复制为
   `local.properties`,并填写以下内容:
   ```properties
   sdk.dir=/path/to/your/Android/sdk
   MAPS_API_KEY=your_google_maps_api_key
   CLOUD_AI_API_KEY=
   ```
   `MAPS_API_KEY` 仅地图/导航镜头需要。`CLOUD_AI_API_KEY` 可留空——云端 AI 为可选功能,
   默认处于关闭状态。
3. **等待 Gradle 同步。** Android Studio 通常会自动提示同步;如未自动提示,可手动执行
   **File → Sync Project with Gradle Files**。
4. **选择产品风味(Product Flavor)。** 使用 `standard` 风味构建包含完整 Google Play
   服务的版本,或使用 `foss` 风味构建不依赖 Google 地图/Play 服务的版本。
5. **选择运行配置。** 在 Build Variants 面板中选择 `standardDebug`(或
   `fossDebug`)。
6. **运行项目。** 连接一台支持 ARCore 的设备(或兼容的模拟器镜像),点击 **Run ▶** 按
   钮即可运行。

### 🔐 权限说明

每项权限都会根据使用场景请求——即仅在你首次使用需要该权限的功能时才会弹出——并会在应用
内的 **隐私中心**(设置 → 隐私)中给出说明:

| 权限 | 是否必需 | 用途 |
|---|---|---|
| 相机 | 核心权限 | 所有 AR 与视觉相关功能 |
| 位置 | 可选 | 附近地点、AR 导航、空间回忆 |
| 麦克风 | 可选 | 向 AI 视觉助手发出免提语音指令 |
| 照片与媒体 | 可选 | 保存拍摄内容、将照片导入空间日志 |
| 通知 | 可选 | 空间提醒与任务截止提醒 |

### 🧪 测试

```bash
# 单元测试(运行于 JVM,无需连接设备)
./gradlew testStandardDebugUnitTest

# 插桩测试(需要连接设备或模拟器)
./gradlew connectedStandardDebugAndroidTest
```

单元测试覆盖了营业时间计算器(跨越午夜的时间段、单日多个时间段、即将打烊/即将营业的阈
值判断、未配置/全周休息等边界情况)、物体跟踪器以及数据仓库行为。插桩测试覆盖了 Room 数
据库结构、WorkManager 集成、Compose 界面,以及已明确记录的运行时场景(相机/位置权限被拒
绝、不支持 AR 的设备、跟踪丢失、低电量、设备过热降频、离线模式、模型缺失、低置信度结果
过滤、前后台切换、设备旋转以及进程被系统终止后的恢复)。

### 🌍 本地化

SPECTRA 提供完整的 **英文**、**波斯语** 与 **中文** 翻译。波斯语以真正的从右至左方式呈
现,英文与中文则以从左至右方式呈现。应用内的语言设置可独立于系统语言进行切换。

### 📄 许可证

本项目基于 **MIT 许可证** 发布。完整条款请参见你添加到仓库中的 `LICENSE` 文件。

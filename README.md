# 📦 LastMileTracker

A full-stack **Last-Mile Delivery & Logistics Management Platform** built with **Spring Boot 3** and **React**. It features role-based access control, automated volumetric rate calculation, email OTP verification, real-time agent assignment, and an interactive portal UI.

---

## ✨ Key Features

### 🔐 Authentication & Security
* **Role-Based Access Control (RBAC):** Dedicated portals and API permissions for **Customers (`USER`)**, **Delivery Agents (`AGENT`)**, and **Administrators (`ADMIN`)**.
* **Email OTP Verification:** 6-digit, 5-minute expiring OTP verification for new customer registrations.
* **Strict Data Isolation:** Customers can only view, track, and reschedule their own orders.

### 🚚 Logistics & Order Management
* **Interactive 3-Step Order Wizard:** Captures pickup/drop addresses, pincodes, package dimensions ($L \times B \times H$), actual weight, order type (`B2B`/`B2C`), and payment mode (`PREPAID`/`COD`).
* **Smart Rate Engine:** Automatically detects pickup/drop zones, computes volumetric weight ($\frac{L \times B \times H}{5000}$), bills on the higher of actual vs. volumetric weight, applies the matching intra/inter-zone rate card, and adds COD surcharges—previewed before confirmation.
* **Agent Assignment:** Supports both automatic nearest-available-agent assignment and manual admin assignment.
* **Granular Status Tracking:** Full timeline logging across `CREATED`, `ASSIGNED`, `PICKED_UP`, `IN_TRANSIT`, `OUT_FOR_DELIVERY`, `DELIVERED`, `FAILED`, and `RESCHEDULED`.
* **Failed Delivery Rescheduling:** Customers can pick a new delivery date/time for failed orders, triggering automatic agent reassignment.
* **Asynchronous Email Notifications:** Non-blocking `@Async` email alerts sent via SMTP (Brevo/Gmail) on OTP requests and every order status transition.

### 🛠️ Admin Operations Center
* Manage **Zones**, map **Areas (Pincodes)** to zones, and configure **Rate Cards** (`B2B` & `B2C`).
* Create orders on behalf of customers.
* Filter all network orders by **Status**, **Zone**, or **Agent**, and override any order status.

### 🎨 Interactive Frontend
* Single-page React interface served directly by Spring Boot with Tailwind CSS.
* Features 3D mouse-tracking tilt cards, a reactive custom cursor, and a click-ripple particle network background.

---

## 🧰 Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java, Spring Boot 3.2, Spring Security, Spring Data JPA, Spring Mail (`@Async`), Lombok |
| **Database** | H2 Database (File-based persistence) |
| **Frontend** | React 18 (CDN/Babel), Tailwind CSS, HTML5 Canvas |
| **Build Tool** | Apache Maven |

---

## 🚀 Getting Started

### Prerequisites
* Java 17+ (or Java 21/25)
* Maven 3.8+
* *(Optional)* A free [Brevo](https://www.brevo.com/) SMTP account for live OTP and status emails.

### 1. Clone the Repository
```bash
git clone [https://github.com/aryankushwaha462-svg/lastmile-delivery-tracker.git](https://github.com/aryankushwaha462-svg/lastmile-delivery-tracker.git)
cd lastmile-delivery-tracker

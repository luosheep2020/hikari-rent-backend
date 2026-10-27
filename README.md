# Hikari Rent Backend

**くらしに、ひかりを。 — Bringing light to everyday living.**

A Spring Boot backend for a Japanese rental housing platform.

日本の賃貸住宅検索サービスをテーマにしたSpring Bootバックエンドです。

[English](#english) · [日本語](#日本語)

## English

### Overview

Hikari Rent is a portfolio project for searching and managing rental properties in Japan. The backend provides APIs for the customer frontend and the administration dashboard.

The project is under active development. Property listings are demonstration data.

### Technology Stack

| Technology | Purpose |
| --- | --- |
| Java 21 / Spring Boot 4 | Backend application |
| Spring Security / JWT | Authentication and role-based authorization |
| MyBatis-Plus / MyBatis XML | Data access, pagination, and SQL queries |
| MySQL 8+ | Relational database (`hikari_rent`) |
| Spring Data Redis | Caching, login sessions, and browsing history |
| Spring Data Elasticsearch / Elasticsearch 8 | Property search and index synchronization |
| Cloudflare R2 / AWS SDK for Java v2 | Image storage through the S3-compatible API |
| Jakarta Validation / Lombok | Request validation and boilerplate reduction |
| Maven / Docker | Build and infrastructure tooling |

### Features

- Property search with pagination, sorting, and filters for rent, area, station, layout, property type, and amenities.
- Property details and map-based search.
- Hierarchical area and railway operator / line / station selection, with Redis caching.
- JWT authentication, Redis-backed login sessions, profiles, and avatars.
- Favorites, browsing history, and message session / record APIs.
- File metadata and image storage integration with Cloudflare R2.
- Administrative property creation, editing, review, publication, withdrawal, and rented status.
- Company, department-tree, and position management.
- Transactional search-index sync tasks with retries.

Management access is divided into four roles:

| Role | Scope |
| --- | --- |
| `USER` | Personal account features |
| `AGENT` | Assigned properties within the employee's company |
| `COMPANY_ADMIN` | Company properties and organization management |
| `PLATFORM_ADMIN` | Platform-wide management and property reviews |

### Architecture

The backend is a modular monolith: business packages share one Spring Boot application and database.

**Controller → Service → Repository → Mapper → MySQL**

| Layer | Responsibility |
| --- | --- |
| Controller | HTTP endpoints, request binding, and validation |
| Service | Business rules, authorization scope, and transactions |
| Repository | Business-oriented data access operations |
| Mapper | MyBatis mapping and SQL execution |

SQL is maintained in XML files. DTOs define request data, and VOs define API responses. Spring Security controls endpoint access; service logic and SQL enforce company and property ownership.

Redis supports caching and user activity. Elasticsearch serves property search; MySQL remains the source of truth, with index updates processed through database-backed sync tasks. Images are stored in R2, while file metadata and storage keys are stored in MySQL.

### Project Structure

Paths below are relative to `src/main/java/com/hikari/rent/`.

| Package | Responsibility |
| --- | --- |
| `auth` | Authentication, JWT, sessions, and current-user context |
| `user` | Profiles, favorites, and browsing history |
| `rental` | Public and administrative property APIs, search, and index sync |
| `company` | Companies, departments, and positions |
| `area` | Administrative area queries and caching |
| `station` | Railway operator, line, and station queries |
| `message` | Message sessions and records |
| `file` | Uploads, file metadata, and image URLs |
| `common` | Shared configuration, responses, exceptions, and utilities |

Business packages contain `controller`, `dto`, `entity`, `repository`, `mapper`, `service`, and `vo` directories as needed. SQL mappings are located in `src/main/resources/mapper/`.

### Running Locally

Prepare JDK 21, MySQL, Redis, and Elasticsearch. Configure database, JWT, storage, and service connection settings through environment variables referenced by `application.yml`.

```bash
./mvnw spring-boot:run
```

Default local API address: `http://localhost:8080`.

## 日本語

### プロジェクト概要

Hikari Rentは、日本の賃貸物件の検索・管理をテーマにしたポートフォリオです。利用者向けフロントエンドと管理画面に必要なAPIを提供します。

現在も開発を進めています。掲載物件はデモデータです。

### 技術スタック

| 技術 | 用途 |
| --- | --- |
| Java 21 / Spring Boot 4 | バックエンドアプリケーション |
| Spring Security / JWT | 認証・ロールに基づく認可 |
| MyBatis-Plus / MyBatis XML | データアクセス・ページネーション・SQL |
| MySQL 8+ | リレーショナルデータベース（`hikari_rent`） |
| Spring Data Redis | キャッシュ・ログインセッション・閲覧履歴 |
| Spring Data Elasticsearch / Elasticsearch 8 | 物件検索・インデックス同期 |
| Cloudflare R2 / AWS SDK for Java v2 | S3互換APIによる画像保存 |
| Jakarta Validation / Lombok | 入力検証・定型コードの削減 |
| Maven / Docker | ビルド・開発基盤 |

### 主な機能

- 賃料・エリア・駅・間取り・物件種別・設備による検索、並び替え、ページネーション。
- 物件詳細表示と地図検索。
- エリア、鉄道会社・路線・駅の階層選択とRedisキャッシュ。
- JWT認証、Redisによるセッション管理、プロフィール・アバター管理。
- お気に入り、閲覧履歴、メッセージセッション・履歴のAPI。
- ファイル情報管理とCloudflare R2への画像保存連携。
- 管理画面向けの物件作成・編集・審査・公開・掲載停止・成約状態の管理。
- 会社、部署ツリー、役職の管理。
- トランザクションと連動した検索インデックス同期タスクと再試行。

| ロール | 操作範囲 |
| --- | --- |
| `USER` | 自分のアカウントに関する機能 |
| `AGENT` | 所属会社内で自分が担当する物件 |
| `COMPANY_ADMIN` | 所属会社の物件・組織管理 |
| `PLATFORM_ADMIN` | プラットフォーム全体の管理・物件審査 |

### アーキテクチャ

業務ごとにパッケージを分けた、単一のSpring Bootアプリケーションです。

**Controller → Service → Repository → Mapper → MySQL**

| レイヤー | 役割 |
| --- | --- |
| Controller | HTTPリクエストの受付・入力検証 |
| Service | 業務ルール・操作範囲・トランザクション管理 |
| Repository | 業務で使用するデータアクセス処理 |
| Mapper | MyBatisによるマッピングとSQL実行 |

SQLはXMLファイルで管理します。リクエストにはDTO、レスポンスにはVOを使用します。Spring SecurityでAPIへのアクセスを制御し、ServiceとSQLで会社・担当物件の操作範囲を制限します。

Redisはキャッシュやユーザー操作情報に利用します。Elasticsearchは物件検索に使用し、正本のデータはMySQLで管理します。検索インデックスはDB上の同期タスクから更新します。画像本体はR2、ファイル情報と保存キーはMySQLに保存します。

### ディレクトリ構成

以下は`src/main/java/com/hikari/rent/`配下の主なパッケージです。

| パッケージ | 役割 |
| --- | --- |
| `auth` | 認証・JWT・セッション・ログインユーザー情報 |
| `user` | プロフィール・お気に入り・閲覧履歴 |
| `rental` | 公開／管理用の物件API・検索・インデックス同期 |
| `company` | 会社・部署・役職 |
| `area` | エリア検索・キャッシュ |
| `station` | 鉄道会社・路線・駅の検索 |
| `message` | メッセージセッション・履歴 |
| `file` | アップロード・ファイル情報・画像URL |
| `common` | 共通設定・レスポンス・例外・ユーティリティ |

各業務パッケージには、必要に応じて`controller`、`dto`、`entity`、`repository`、`mapper`、`service`、`vo`を配置します。SQLのXMLファイルは`src/main/resources/mapper/`に配置します。

### ローカル起動

JDK 21、MySQL、Redis、Elasticsearchを用意し、`application.yml`が参照する環境変数にDB接続、JWT、ストレージなどの設定を指定します。

```bash
./mvnw spring-boot:run
```

ローカルAPIの既定URL：`http://localhost:8080`。

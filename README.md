# Hikari Rent Backend

**くらしに、ひかりを。 — Bringing light to everyday living.**

A Java backend for a Japanese rental housing portfolio project. Built with Spring Boot, MyBatis-Plus, MySQL, and Redis, it provides public property search, property details, hierarchical area data, and railway station selection APIs for the Hikari Rent frontend.

日本の賃貸住宅検索をテーマにしたポートフォリオのバックエンドです。Spring Boot・MyBatis-Plus・MySQL・Redisを使用し、物件検索、物件詳細、エリア階層、鉄道会社・路線・駅の選択に必要なAPIを提供します。

[English](#english) · [日本語](#日本語)

> Status: under active development. The current focus is public search APIs and frontend integration. Authentication, authorization, private messaging, and administrative workflows are planned. Property listings are demonstration data, not verified real rental offers.
>
> 開発中のプロジェクトです。現在は公開検索APIとフロントエンド連携を中心に実装しています。認証・認可、メッセージ、管理機能は今後の開発対象です。掲載物件はデモデータです。

## English

### Overview

Hikari Rent helps users search for rental homes in Japan by area, station, budget, layout, and property features. This repository contains the Spring Boot backend; the Next.js frontend is maintained separately.

The backend is a single application organized into business packages. It uses a four-layer structure to separate HTTP handling, business rules, logical data access, and SQL execution. The project prioritizes a complete, understandable implementation over unnecessary distributed infrastructure.

### Features and current scope

| Area | Implemented scope | Notes |
| --- | --- | --- |
| Rental list | Pagination, name search, municipality, rent range, layouts, building age, station, walking distance, features, sorting | Queries MySQL; returns a dedicated list VO |
| Rental details | Photos, floor plan, fees, building information, facilities, contract information, agent, management company | Public visibility conditions are checked; unknown values may be null |
| Public visibility | Published and approved listings, active publishing company, active agent belonging to that company | Public detail queries use the same visibility rules |
| Area lookup | Direct children by parent code and a complete three-level area tree | Region → prefecture → municipality |
| Station lookup | Keyword search, operator lookup, lines by operator, stations by line, complete tree | Uses active lines and stations |
| Cache | JSON cache helper and separate area/station cache components | Redis failures fall back to MySQL for these tree endpoints |
| API infrastructure | Response envelope, pagination result, validation, global exception handling, request logging, CORS | Error messages and code comments use English |
| File URLs | Converts stored object keys into public image URLs | Actual objects and a public base URL must exist |
| Storage integration | AWS SDK v2 S3 dependency for Cloudflare R2 integration | Upload APIs are not included in the implemented scope described here |

The frontend's demo login, favorites, profile editing, browsing history, and chat UI do not establish that corresponding backend APIs are implemented.

### Technology stack

| Technology | Role |
| --- | --- |
| Java 21 | Application language and runtime |
| Spring Boot 4.1.1 | Application configuration and dependency management |
| Spring MVC | REST endpoints and request binding |
| Jakarta Bean Validation | Request validation through the validation starter |
| MyBatis-Plus 3.5.17 | Entity mapping, CRUD support, pagination, repository base classes |
| MyBatis XML | Conditional SQL and relational queries |
| MySQL 8.0.16+ | Relational storage, foreign keys, enforced CHECK constraints |
| Spring Data Redis | Redis access through `StringRedisTemplate` |
| Lettuce | Default Redis client supplied by the Redis starter |
| Jackson 3 | JSON serialization; versions managed by Spring Boot |
| AWS SDK for Java v2 2.55.16 | S3-compatible storage integration |
| Lombok | Accessors and logging boilerplate reduction |
| Maven | Dependency resolution, compilation, testing, packaging |

The listed versions reflect the development configuration. `pom.xml` is authoritative if dependencies change. Java objects are injected with `jakarta.annotation.Resource`.

#### Dependency management

- Spring Boot's parent manages the Spring ecosystem, Jackson, MySQL driver, Lombok, and Redis starter dependencies.
- `mybatis-plus-bom` aligns MyBatis-Plus artifacts.
- `software.amazon.awssdk:bom` aligns AWS SDK modules.
- Do not independently upgrade only `jackson-databind` while leaving its related dependencies on incompatible managed versions.
- Jackson 3 databind/core imports use `tools.jackson.*`; shared Jackson annotations still use `com.fasterxml.jackson.annotation.*`.

### Architecture

| Layer | Responsibility | Example |
| --- | --- | --- |
| Controller | Bind/validate HTTP input and return `ApiResponse` | `StationController` |
| Service | Apply business rules, assemble VOs, coordinate cache access | `StationServiceImpl` |
| Repository | Expose domain-specific data access operations | `StationRepository` |
| Mapper | Execute SQL through MyBatis | `StationMapper` and XML |

Repositories use `IRepository<Entity>` and implementations extend `CrudRepository<Mapper, Entity>`. The mapper type is the first generic parameter of `CrudRepository`; the entity is the second.

Supporting components have separate responsibilities:

- `JsonCache`: JSON conversion, Redis read/write, TTL, and deletion. It propagates failures to callers.
- `AreaTreeCache` / `StationTreeCache`: cache keys, expiry, and tree-specific fallback policy.
- `FileUrlService`: generates public image URLs from storage keys.
- `repository/model`: SQL result rows used when a query does not map directly to an entity.
- `vo`: API response models; entities are not exposed as the public response contract.

### Project structure

The following tree describes the main files introduced for the current implementation. Additional exception, logging, and configuration files may exist in the repository.

```text
hikari-rent-backend/
├── pom.xml
├── README.md
├── mvnw / mvnw.cmd                 # When Maven Wrapper is committed
└── src/
    ├── main/
    │   ├── java/com/hikari/rent/
    │   │   ├── HikariRentBackendApplication.java
    │   │   ├── common/
    │   │   │   ├── cache/JsonCache.java
    │   │   │   ├── config/          # CORS and MyBatis-Plus configuration
    │   │   │   ├── exception/       # Global exception handling
    │   │   │   ├── filter/          # Request logging
    │   │   │   └── result/
    │   │   │       ├── ApiResponse.java
    │   │   │       └── PageResult.java
    │   │   ├── rental/
    │   │   │   ├── controller/
    │   │   │   ├── dto/             # RentalPageRequest
    │   │   │   ├── entity/          # Rental
    │   │   │   ├── enums/           # Publication and review statuses
    │   │   │   ├── exception/
    │   │   │   ├── mapper/
    │   │   │   ├── repository/
    │   │   │   │   ├── impl/
    │   │   │   │   └── model/       # Image, station, feature, agent, company rows
    │   │   │   ├── service/impl/
    │   │   │   └── vo/             # List/detail, agent, station, company VOs
    │   │   ├── area/
    │   │   │   ├── cache/AreaTreeCache.java
    │   │   │   ├── controller/AreaController.java
    │   │   │   ├── entity/Area.java
    │   │   │   ├── mapper/AreaMapper.java
    │   │   │   ├── repository/impl/
    │   │   │   ├── service/impl/
    │   │   │   └── vo/             # AreaVO, AreaTreeVO
    │   │   ├── station/
    │   │   │   ├── cache/StationTreeCache.java
    │   │   │   ├── controller/StationController.java
    │   │   │   ├── dto/StationSearchRequest.java
    │   │   │   ├── entity/Station.java
    │   │   │   ├── mapper/StationMapper.java
    │   │   │   ├── repository/
    │   │   │   │   ├── impl/StationRepositoryImpl.java
    │   │   │   │   └── model/       # StationSearchRow, StationTreeRow
    │   │   │   ├── service/impl/StationServiceImpl.java
    │   │   │   └── vo/             # StationSearchVO, StationOptionVO, StationTreeVO
    │   │   └── file/
    │   │       └── service/FileUrlService.java
    │   └── resources/
    │       ├── application.yml
    │       └── mapper/
    │           ├── rental/RentalMapper.xml
    │           └── station/StationMapper.xml
    └── test/                       # Tests added as development progresses
```

`area` and `station` are packages in the same application, not separate Maven services. The railway operator/line/station queries share the station module rather than creating three additional business modules.

### Local setup

#### Requirements

- JDK 21.
- Maven or the repository's Maven Wrapper.
- MySQL 8.0.16+ with database `hikari_rent`.
- A local or hosted Redis instance for cache verification.
- Optional public image storage for property photos.

#### Database initialization

1. Import the base schema and demo data into `hikari_rent`.
2. Import the area dataset with coordinates.
3. Apply the railway schema migration if starting from the original station table.
4. Import the converted railway data into the final schema.

SQL files were prepared separately during development. If they are not committed in the repository, obtain them before database initialization. A migration for an existing schema is not interchangeable with a clean-install script. Do not rerun a one-time schema migration against tables it has already altered.

The railway data import uses source codes to update existing imported records. Legacy rows without source codes are retained; they are not automatically matched or deleted. Existing rental-to-station relationships must point to the intended internal station IDs.

#### Configuration

The following is a configuration example. Merge it with the actual `application.yml`; do not create duplicate top-level `spring` keys.

```yaml
server:
  port: 8080

spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      username: ${REDIS_USERNAME:default}
      password: ${REDIS_PASSWORD:}
      database: 0
      connect-timeout: 5s
      timeout: 3s
      ssl:
        enabled: ${REDIS_SSL_ENABLED:false}

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true

storage:
  public-base-url: ${R2_PUBLIC_BASE_URL:}
```

Example environment variable values:

```dotenv
DB_URL=jdbc:mysql://localhost:3306/hikari_rent
DB_USERNAME=hikari_app
DB_PASSWORD=replace_with_database_password
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_USERNAME=default
REDIS_PASSWORD=
REDIS_SSL_ENABLED=false
R2_PUBLIC_BASE_URL=
```

- These variable names must match the placeholders in the actual configuration. Adjust an existing project configuration consistently rather than defining unused variables.
- Spring Boot does not automatically load a `.env` file. Use the IDE's environment-variable settings, an explicitly enabled EnvFile plugin, or shell environment variables.
- Enable Redis TLS only when required by the Redis service. Use its database username/password, not the cloud account login password.
- `R2_PUBLIC_BASE_URL` is the public image-serving domain, not the authenticated S3 API endpoint. A URL alone does not upload or create an object.
- Application/database timestamps are intended to use UTC. Configure the database session and application consistently; a column comment does not enforce a timezone.
- Keep secrets outside Git and keep Redis credentials on the backend.

#### Run and build

From the backend project root:

```bash
./mvnw spring-boot:run
```

If the wrapper is not included:

```bash
mvn spring-boot:run
```

Build and run tests:

```bash
./mvnw clean verify
```

Package and launch the executable JAR:

```bash
./mvnw clean package
java -jar target/hikari-rent-backend-0.0.1-SNAPSHOT.jar
```

Use the actual JAR filename if the project version changes. Build commands are provided for local verification; this README does not claim an independently verified CI run or a particular test coverage percentage.

### API reference

Default local base URL: `http://localhost:8080`.

#### Response contract

```java
public record ApiResponse<T>(
    Integer code,
    String message,
    T data
) {
}
```

```java
public record PageResult<T>(
    long pageNum,
    long pageSize,
    long total,
    List<T> records
) {
}
```

Success uses `code: 0`. The `data` field is typed as `T`, not `Object`. Failure responses also use appropriate HTTP status codes; a public rental that cannot be viewed is handled as HTTP 404.

Example page envelope:

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "pageNum": 1,
    "pageSize": 6,
    "total": 0,
    "records": []
  }
}
```

#### Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/rentals` | Search public properties |
| GET | `/api/rentals/{id}` | Public property details |
| GET | `/api/areas` | Direct children; omit `parentCode` for root regions |
| GET | `/api/areas/tree` | Complete region/prefecture/municipality tree |
| GET | `/api/stations` | Station keyword search |
| GET | `/api/stations/operators` | Active railway operators with available stations |
| GET | `/api/stations/lines` | Active lines by `operatorCode` |
| GET | `/api/stations/options` | Active station options by internal `lineId` |
| GET | `/api/stations/tree` | Complete operator/line/station tree |

The separate station option endpoints remain available, although the current frontend selector uses the complete tree for cross-level searching.

#### Rental search parameters

| Parameter | Meaning | Example |
| --- | --- | --- |
| `page` | One-based page number | `1` |
| `pageSize` | Page size, currently 1–50 | `6` |
| `keyword` | Property name keyword | Japanese property name |
| `areaCode` | Exact five-digit municipality code | `13104` |
| `minRent`, `maxRent` | Monthly base rent bounds in JPY | `80000`, `150000` |
| `floorPlans` | Repeated layout values | `floorPlans=1K&floorPlans=1LDK` |
| `maxBuildingAge` | Maximum building age in years | `10` |
| `stationId` | Internal station primary key | A returned station ID |
| `maxWalkingMinutes` | Maximum station walking time | `10` |
| `featureCodes` | Repeated feature codes from the database | `featureCodes=...&featureCodes=...` |
| `sort` | Sort mode | `rentAsc` |

Supported sort values are `recommended`, `rentAsc`, `rentDesc`, `areaDesc`, and `walkingAsc`. The default/recommended branch uses publication time and ID; it is not a recommendation algorithm. A frontend value such as `newest` must be mapped to a supported backend value before integration.

Search rules:

- Different categories are combined with AND.
- Multiple layouts are combined with OR.
- Every selected feature code must match the property.
- A station ID and walking limit must match the same rental-station relationship.
- Rent bounds refer to base rent; management fees are returned separately.
- A region or prefecture code is not accepted as an exact municipality filter. Descendant-area expansion is a separate enhancement.
- The backend parameter is `page`; the response field is `pageNum`.

Examples:

```bash
curl 'http://localhost:8080/api/rentals?page=1&pageSize=6&maxRent=150000&sort=rentAsc'
curl 'http://localhost:8080/api/rentals?areaCode=13104&page=1&pageSize=6'
curl 'http://localhost:8080/api/rentals/10001'
```

#### Area queries

```bash
curl 'http://localhost:8080/api/areas'
curl 'http://localhost:8080/api/areas?parentCode=kanto'
curl 'http://localhost:8080/api/areas?parentCode=13'
curl 'http://localhost:8080/api/areas/tree'
```

Area nodes include names, codes, hierarchy levels, and coordinates. Area codes are identifiers rather than postal codes. Strings preserve values such as `01` and support region codes such as `kanto`.

#### Station queries

```bash
curl --get 'http://localhost:8080/api/stations' \
  --data-urlencode 'keyword=新宿' \
  --data-urlencode 'prefectureCode=13' \
  --data-urlencode 'limit=20'

curl 'http://localhost:8080/api/stations/operators'
curl 'http://localhost:8080/api/stations/tree'
```

For dependent lookups, use values returned by the preceding endpoint:

```text
/api/stations/lines?operatorCode=<operator-source-code>
/api/stations/options?lineId=<internal-line-id>
/api/rentals?stationId=<internal-station-id>
```

Keyword search returns an empty list for a blank keyword and allows a maximum limit of 20. The full tree is a separate endpoint and is not limited to 20 stations.

Tree nodes use `value`, `label`, and `children`:

- Operator value: source operator code.
- Line value: internal line ID represented as a string.
- Station value: internal station ID represented as a string.

The frontend may search a complete path by operator, line, or station label. Selecting a leaf submits only `stationId` to rental search. Same-name stations on different lines remain distinct records; station-group matching across lines is not yet implemented.

### Database design and imported data

Database name: **`hikari_rent`**.

#### Main table groups

| Group | Tables | Responsibility |
| --- | --- | --- |
| Properties | `rentals`, `rental_images`, `rental_stations` | Listings, photos/floor plans, access information |
| Features | `rental_features`, `rental_feature_relations` | Amenities and occupancy conditions |
| Geography | `areas` | Region/prefecture/municipality hierarchy |
| Railways | `railway_lines`, `stations` | Railway lines and station-on-line records |
| Organizations | `companies`, `departments`, `positions` | Real-estate companies and staff organization |
| Identity | `users`, `roles`, `permissions`, `user_roles`, `role_permissions` | User/RBAC schema prepared for authentication work |
| Files | `files` | Object metadata and storage keys |
| User activity | `favorites`, `browsing_history` | Schema for later persistent user features |
| Messaging | `conversations`, `messages`, `message_attachments` | Schema for later private messaging |

A database table existing does not imply that its application APIs or authorization checks are complete.

#### Status conventions

Status fields use numeric TINYINT values with explicit meanings.

| Field | Mapping |
| --- | --- |
| `rentals.status` | `0` draft, `1` published, `2` rented, `3` archived |
| `rentals.review_status` | `0` not submitted, `1` pending, `2` approved, `3` rejected |
| `railway_lines.status`, `stations.status` | `0` inactive, `1` active, `2` planned |
| User/company activation | `0` disabled, `1` active |

Publication and review are separate dimensions. Public property queries require `status = 1` and `review_status = 2` together with company/agent checks.

#### Area dataset

The imported coordinate dataset contains 1,960 records: 8 regions, 47 prefectures, and 1,905 municipalities. These are counts from the imported source snapshot, not a claim about a permanently fixed administrative count.

Parent references are stored in `parent_code`. The database checks valid hierarchy levels and parent presence, while import/application logic must verify that each child belongs to the appropriate parent level. Coordinate range checks do not prove geographic accuracy.

#### Railway dataset

Imported snapshots from [駅データ.jp](https://ekidata.jp/):

| Source file | Rows | Usage |
| --- | --- | --- |
| `../../Downloads/chrome/company20260409.csv` | 175 | Resolves operator names; not imported into real-estate `companies` |
| `../../Downloads/chrome/line20260618free.csv` | 624 | Railway lines, including source status information |
| `../../Downloads/chrome/station20260731free.csv` | 10,962 | Station-on-line records, including inactive records |

The station snapshot includes 10,465 source-active records and 497 abolished records. Public lookup queries additionally require an active associated line.

- External source codes are stored separately from internal primary keys.
- `station_group_code` preserves the source grouping identifier; names are not globally unique.
- Source `e_status` is translated: `0 → 1`, `1 → 2`, `2 → 0`.
- Source prefecture codes are padded to two digits, such as `1 → 01`.
- Municipality `area_code` remains null until reliably matched. The CSV's prefecture code must not be substituted for a municipality code.
- The migration schema temporarily retains `stations.line_name` for compatibility. New station queries join `railway_lines`; remove the duplicated column only after all readers/writers migrate.
- Railway `operator_source_code` is unrelated to the real-estate `companies.id`.

See the provider's [data specification](https://ekidata.jp/doc/) and [terms](https://ekidata.jp/agreement.php) when using or redistributing a dataset. The free dataset has omissions, including station kana/romanization and Shinkansen station records. Blank imported fields remain unknown; they are not generated automatically.

### Redis cache design

| Key | Payload | Expiry |
| --- | --- | --- |
| `hikari:areas:tree:v1` | Complete area tree | 24 hours |
| `hikari:stations:tree:v1` | Complete active railway tree | 24 hours |

Tree request behavior:

1. Try Redis.
2. On a hit, deserialize JSON and return the tree.
3. On a miss or recoverable cache-read failure, query MySQL and build the tree.
4. Try writing JSON with a TTL, then return the database result even if the cache write fails.

`StringRedisTemplate` stores string keys and values. `JsonMapper` handles object/JSON conversion; no JDK object serialization or `Serializable` requirement is introduced for tree VOs.

Cache eviction failures propagate to callers rather than claiming success. After a data import or a committed area/station update, evict the relevant tree key. Until expiry or successful eviction, clients may receive the previous snapshot. There is no distributed cache-rebuild lock or circuit breaker at this stage; simultaneous misses can rebuild the same tree, and a Redis outage may add timeout latency before fallback.

For a local Redis instance:

```bash
redis-cli TTL hikari:areas:tree:v1
redis-cli TTL hikari:stations:tree:v1
redis-cli DEL hikari:areas:tree:v1 hikari:stations:tree:v1
```

For hosted Redis, use the corresponding endpoint, authentication, and TLS settings in a Redis client. Do not paste passwords into public logs or repository files.

### Frontend integration

The frontend runs locally at `http://localhost:3000`, with the backend at `http://localhost:8080`.

- The frontend API client uses `credentials: 'include'`.
- Backend CORS allows the explicit frontend origin and credentials. A wildcard `Access-Control-Allow-Origin: *` is incompatible with credentialed browser requests.
- Production origins must be configured explicitly when deploying.
- Area selection submits a municipality `areaCode`.
- Station selection displays an operator/line/station path but submits only the leaf `stationId`.
- Public image URLs are separate from Redis access and must resolve to actual uploaded image objects.

Successful area and station selection does not by itself complete rental-list integration. The list page must read URL conditions, call `/api/rentals`, and use server pagination and sorting.

### Troubleshooting

| Symptom | Cause / next step |
| --- | --- |
| `JsonApplyView` class not found | Check for independently overridden Jackson dependencies; restore a compatible Boot-managed set |
| COUNT query has no placeholders but parameters are bound | The current rental page disables COUNT optimization with `page.setOptimizeCountSql(false)`; retain it unless the SQL rewrite issue is resolved and verified |
| `redisTemplate` bean has the wrong type | With field injection, use `@Resource(name = "stringRedisTemplate")` for `StringRedisTemplate` |
| `Unable to connect to Redis` | Inspect the nested cause; check loaded environment variables, host/port, database credentials, and TLS |
| `.env` changes have no effect | Explicitly load the file in the run configuration and fully restart the process |
| `Broken pipe` while writing JSON | Client disconnected/cancelled; verify the final browser request before treating it as a business failure |
| CORS rejection with credentials | Replace wildcard origins with the actual frontend origin |
| Images are null or broken | Check image relations, active file metadata, public base URL, and the existence of the storage object |
| Station exists but rental search returns none | Create or verify `rental_stations` relationships using the imported internal station ID |
| Imported area/station data does not appear | Evict the appropriate Redis tree key and retry |

### Verification checklist

Before publishing changes, run checks appropriate to the implementation:

- Build the project and run the existing tests.
- Verify filtering, ascending/descending rent sorting, total counts, and page boundaries.
- Verify nonexistent/unpublished property details return HTTP 404.
- Verify photo, station, and feature relations are assembled into responses.
- Verify root/child area lookup and both complete trees.
- Verify inactive stations/lines are excluded from public lookup.
- Verify initial cache miss, subsequent cache hit, and database fallback during a Redis failure.
- Verify the frontend can call the API under its actual origin.

### Roadmap

- Spring Security authentication, registration, password hashing, and login restoration.
- RBAC authorization based on the prepared roles/permissions schema.
- Login-gated agent phone retrieval enforced on the backend.
- Persistent favorites, browsing history, and profile editing.
- Validated file upload to Cloudflare R2.
- Map-bounds rental queries.
- Property creation, editing, review, publication, and archival workflows.
- Conversation/message APIs, attachments, unread counts, and access control.
- Incremental automated tests for critical business rules and integration paths.

## 日本語

### プロジェクト概要

Hikari Rent Backendは、日本の賃貸住宅をエリア・駅・予算・間取り・設備から探すためのREST APIです。Next.jsのフロントエンドとは別のリポジトリで管理する、単体のSpring Bootアプリケーションです。

求職用ポートフォリオとして、検索から詳細表示までの流れ、データの関連付け、レイヤー分割、キャッシュ、フロントエンド連携を実装しています。物件情報はサンプルであり、実際の募集情報を保証するものではありません。

### 機能と実装範囲

| 分類 | 実装内容 | 補足 |
| --- | --- | --- |
| 物件一覧 | ページネーション、物件名、エリア、賃料、間取り、築年数、駅、徒歩分数、設備条件、並び替え | MySQLで絞り込み |
| 物件詳細 | 写真、間取り図、費用、建物、設備、契約、担当者、管理会社 | 不明な値はnullなどで表現 |
| 公開条件 | 公開中・審査承認済み・掲載会社有効・担当者有効かつ所属一致 | 一覧と公開詳細に適用 |
| エリア | 親コードからの子取得、地方→都道府県→市区町村のツリー | 座標データをインポート |
| 駅 | キーワード検索、鉄道会社→路線→駅の個別取得、完全なツリー | 有効な路線・駅のみ公開検索対象 |
| Redis | エリア・駅ツリーのJSONキャッシュ | 読み書き失敗時はDB結果を利用 |
| 共通処理 | レスポンス、入力検証、例外処理、ログ、CORS | コードコメント・エラーメッセージは英語 |
| 画像URL | ストレージキーから公開URLを生成 | 実ファイルと公開ドメインが必要 |

認証、お気に入り、プロフィール、閲覧履歴、チャットのフロントエンドUIが存在していても、それぞれのバックエンドAPIが完成していることを意味しません。

### 技術スタック

| 技術 | 用途 |
| --- | --- |
| Java 21 | 開発言語・実行環境 |
| Spring Boot 4.1.1 / Spring MVC | アプリケーション構成・REST API |
| Jakarta Bean Validation | リクエスト検証 |
| MyBatis-Plus 3.5.17 / MyBatis XML | CRUD、ページネーション、動的SQL |
| MySQL 8.0.16以降 | データ保存、外部キー、CHECK制約 |
| Spring Data Redis / Lettuce | Redis接続とキャッシュ操作 |
| Jackson 3 | JSON変換 |
| AWS SDK for Java v2 2.55.16 | S3互換ストレージ連携 |
| Lombok / Maven | 記述量削減・依存管理・ビルド |

正確な依存バージョンは`pom.xml`を参照してください。Spring関連はBootの親POM、MyBatis-PlusとAWS SDKは各BOMでバージョンを揃えます。Jacksonだけを個別に更新し、関連ライブラリと不整合にしない構成です。

### レイヤーとディレクトリ

基本構成は、**Controller → Service → Repository → Mapper**です。

| 層・ディレクトリ | 役割 |
| --- | --- |
| `controller` | HTTPパラメータの受け取り、入力検証、レスポンス |
| `service` / `service.impl` | 業務条件、VO組み立て、キャッシュとの連携 |
| `repository` / `repository.impl` | 業務単位のデータ取得操作 |
| `mapper` / XML | SQL実行 |
| `entity` | テーブルに対応するモデル |
| `dto` | 検索条件などの入力モデル |
| `vo` | フロントエンドへ返すデータ |
| `repository.model` | JOIN等のSQL結果を受け取るモデル |
| `cache` | 各モジュールのキー、TTL、キャッシュ障害時の方針 |

主要パッケージは以下のとおりです。詳細ツリーは英語版の「Project structure」に記載しています。

| パッケージ | 内容 |
| --- | --- |
| `com.hikari.rent.common` | 共通キャッシュ、設定、例外、ログ、レスポンス |
| `com.hikari.rent.rental` | 物件一覧・詳細・関連情報 |
| `com.hikari.rent.area` | エリアの子取得・ツリー・キャッシュ |
| `com.hikari.rent.station` | 鉄道会社・路線・駅の検索とツリー |
| `com.hikari.rent.file` | ストレージ関連処理、公開URL生成 |

`area`と`station`は同じアプリケーション内のパッケージです。マイクロサービスや別Mavenモジュールではありません。DIには`@Resource`を使用します。`StringRedisTemplate`をフィールド名`redisTemplate`へ注入する場合は、`@Resource(name = "stringRedisTemplate")`で対象Beanを明示します。

Repositoryは`IRepository<Entity>`、実装は`CrudRepository<Mapper, Entity>`を利用します。基本CRUDメソッドをすべて独自実装する必要はありません。

### ローカル起動

#### 必要な環境

- JDK 21。
- Maven、またはコミット済みのMaven Wrapper。
- MySQL 8.0.16以降。DB名は`hikari_rent`。
- キャッシュ確認用のRedis。ローカル・クラウドのどちらでも利用可能。

#### 初期データ

1. ベースのスキーマとデモデータをインポートします。
2. 座標付きエリアデータをインポートします。
3. 古い駅テーブルを使用している場合は、対応する路線・駅のマイグレーションを実行します。
4. 変換済みの路線・駅データをインポートします。

SQLファイルがリポジトリに含まれていない場合は、事前に別途用意してください。既存DB用マイグレーションと新規構築用DDLは用途が異なります。一度適用したマイグレーションをそのまま再実行しないでください。

#### 接続設定

英語版「Configuration」のYAML例を、実際の`application.yml`と整合させます。主な環境変数は以下です。

| 変数 | 内容 |
| --- | --- |
| `DB_URL` | MySQLのJDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | DB接続情報 |
| `REDIS_HOST` / `REDIS_PORT` | Redis接続先 |
| `REDIS_USERNAME` / `REDIS_PASSWORD` | Redis DBの認証情報 |
| `REDIS_SSL_ENABLED` | 接続先が要求する場合にTLSを有効化 |
| `R2_PUBLIC_BASE_URL` | 画像配信用の公開ベースURL |

この一覧はYAML例と組み合わせて使用します。実装済み設定に別名の変数がある場合は、プレースホルダーと環境変数を一致させてください。

Spring Bootは`.env`を自動では読み込みません。IDEの環境変数、明示的に有効化したEnvFileプラグイン、またはシェルで設定します。環境変数変更後はプロセスを再起動します。

Redisはクラウドアカウントのログインパスワードではなく、DB接続用の資格情報を使用します。TLSの有無もプロバイダー設定に合わせます。秘密情報はGitへコミットしません。

日時はUTCを前提に扱います。アプリケーション・DBセッションのタイムゾーンを揃えてください。カラムコメントにUTCと記載するだけでは変換は行われません。

#### 起動コマンド

```bash
./mvnw spring-boot:run
```

Wrapperがない場合は`mvn spring-boot:run`を使用します。

```bash
./mvnw clean verify
./mvnw clean package
java -jar target/hikari-rent-backend-0.0.1-SNAPSHOT.jar
```

成果物名は実際のバージョンに合わせます。上記は確認用コマンドであり、本READMEはCI実行結果やテストカバレッジを保証するものではありません。

### API

ローカルのベースURL：`http://localhost:8080`。

| メソッド | パス | 内容 |
| --- | --- | --- |
| GET | `/api/rentals` | 公開物件の条件検索 |
| GET | `/api/rentals/{id}` | 公開物件詳細 |
| GET | `/api/areas` | `parentCode`直下のエリア。省略時は地方 |
| GET | `/api/areas/tree` | 全エリアツリー |
| GET | `/api/stations` | 駅名キーワード検索 |
| GET | `/api/stations/operators` | 選択可能な鉄道会社 |
| GET | `/api/stations/lines` | `operatorCode`に属する路線 |
| GET | `/api/stations/options` | `lineId`に属する駅 |
| GET | `/api/stations/tree` | 鉄道会社→路線→駅の全ツリー |

レスポンスは`code`、`message`、`data`を持ち、成功コードは`0`です。`ApiResponse<T>`の`data`には`T`を使用します。ページ結果は`pageNum`、`pageSize`、`total`、`records`です。

#### 物件検索

主なパラメータ：

| パラメータ | 内容 |
| --- | --- |
| `page` / `pageSize` | 1始まりのページ、ページサイズ1〜50 |
| `keyword` | 物件名 |
| `areaCode` | 市区町村の5桁コード |
| `minRent` / `maxRent` | 月額基本賃料の範囲、円単位 |
| `floorPlans` | 間取り。複数は同名パラメータを繰り返す |
| `maxBuildingAge` | 最大築年数 |
| `stationId` / `maxWalkingMinutes` | 駅IDと徒歩分数 |
| `featureCodes` | DB上の設備・条件コードを複数指定 |
| `sort` | `recommended`、`rentAsc`、`rentDesc`、`areaDesc`、`walkingAsc` |

異なる条件群はAND、複数の間取りはOR、設備コードはすべて一致する条件です。駅IDと徒歩分数は同じ駅との関連で一致する必要があります。管理費は賃料の絞り込み金額に含めず、別フィールドとして返します。

リクエストは`page`、レスポンスは`pageNum`です。地方・都道府県コードから配下すべてを検索する処理は別途拡張が必要です。「おすすめ順」は現在、公開日時などによる既定の並び順であり、推薦エンジンではありません。フロントエンドの`newest`などはバックエンドが対応する値へ合わせてください。

公開詳細では非公開・未承認・存在しない物件をHTTP 404として扱います。公開レスポンスの担当者電話番号はnullとし、ログイン必須の電話取得APIは今後実装します。

#### エリア検索

- `/api/areas`：地方一覧。
- `/api/areas?parentCode=kanto`：関東に属する都道府県。
- `/api/areas?parentCode=13`：東京都に属する市区町村。
- `/api/areas/tree`：全階層をまとめて取得。

コードは郵便番号ではありません。`01`の先頭ゼロと`kanto`等の地方コードを保持するため、文字列で管理します。

#### 駅検索と選択

駅名検索は`keyword`、任意の`prefectureCode`、最大20件の`limit`を受け取ります。空のキーワードでは空配列を返します。完全ツリーにはこの20件制限は適用しません。

ツリーの`value`は、鉄道会社が外部の会社コード、路線が内部の路線ID、駅が内部の駅IDです。フロントエンドでは会社名・路線名・駅名から完全パスを検索できますが、物件検索へ送る値は最後に選択した`stationId`だけです。

同じ名前でも路線が異なる駅は別レコードです。駅グループを利用して複数路線を横断検索する処理は未実装です。

### データベースとデータソース

DB名は`hikari_rent`です。主なテーブル群と用途は英語版「Database design and imported data」の表を参照してください。RBAC、お気に入り、閲覧履歴、メッセージ等のテーブルがあっても、その機能のAPI完成を意味するものではありません。

#### 数値ステータス

| 対象 | 値 |
| --- | --- |
| 物件公開状態 | `0`下書き、`1`公開中、`2`成約済み、`3`アーカイブ |
| 物件審査状態 | `0`未申請、`1`審査待ち、`2`承認、`3`却下 |
| 路線・駅 | `0`無効、`1`有効、`2`予定 |
| ユーザー・会社 | `0`無効、`1`有効 |

物件の公開状態と審査状態は別フィールドです。

#### エリアデータ

インポートしたスナップショットは地方8件、都道府県47件、市区町村1,905件、計1,960件です。行政区画の件数が今後も固定されることを意味しません。座標の数値範囲は検証していますが、それだけで地理的な正確性を保証するものではありません。

#### 鉄道データ

[駅データ.jp](https://ekidata.jp/)の会社・路線・駅CSVを変換して使用しています。

- `../../Downloads/chrome/company20260409.csv`：175件。鉄道会社名の対応付けに使用。
- `../../Downloads/chrome/line20260618free.csv`：624件。
- `../../Downloads/chrome/station20260731free.csv`：10,962件。有効相当10,465件、廃止497件を含む元スナップショット。

鉄道会社を不動産会社用の`companies`テーブルに混在させません。外部コードと内部IDを分離し、インポート時の再更新に利用します。元の状態値はプロジェクトの定義へ変換します。`pref_cd=1`は`01`へ補完します。

CSVには市区町村コードがないため、`area_code`は正確に対応付けるまでnullです。都道府県コードを市区町村コードの代わりには使用しません。`station_group_code`は保持しますが、駅名のみで重複排除しません。

互換性のため残している`stations.line_name`は、すべての読み書きを新方式へ移行するまで削除しません。新しい検索では`railway_lines`をJOINします。

データ利用・再配布時は提供元の[仕様](https://ekidata.jp/doc/)と[利用規約](https://ekidata.jp/agreement.php)を確認してください。無料版にない仮名・ローマ字・新幹線駅等は自動補完していません。

### Redisキャッシュ

| キー | 内容 | TTL |
| --- | --- | --- |
| `hikari:areas:tree:v1` | エリア全ツリー | 24時間 |
| `hikari:stations:tree:v1` | 有効な鉄道会社・路線・駅ツリー | 24時間 |

最初にRedisを読み、未登録・読み取り失敗ならMySQLから取得してツリーを組み立てます。その結果をRedisへ保存し、書き込みに失敗してもDB結果は返します。

`StringRedisTemplate`は文字列として保存し、`JsonMapper`がJSON変換を担当します。ツリーVOにJava標準シリアライズを適用する構成ではありません。

キャッシュ削除の失敗は呼び出し元へ通知します。データ変更のコミット後や再インポート後には、対象キーを削除してください。TTLだけに依存すると、最大24時間は古いスナップショットを返す可能性があります。

現段階では分散ロックによる再生成の排他制御やサーキットブレーカーは実装していません。同時ミスでは再生成が重複し、Redis障害時にはタイムアウト分の遅延が生じる場合があります。

### フロントエンド連携

ローカルではフロントエンド`http://localhost:3000`、バックエンド`http://localhost:8080`を使用します。Cookie送信を伴うクライアント設定に合わせ、CORSでは許可元を明示し、credentialsを許可します。許可元`*`との組み合わせは使用しません。

エリア・駅の選択が成功しても、物件一覧のAPI連携は別途必要です。一覧ページでURLの`areaCode`・`stationId`等を読み、バックエンド検索・並び替え・ページネーションへ渡します。画像表示には実際のストレージオブジェクトも必要です。

### 動作確認と注意点

- ビルドと既存テストを実行する。
- 条件の組み合わせ、並び順、総件数、ページ境界を確認する。
- 公開不可の物件が404になることを確認する。
- 写真・駅・設備の関連データが返ることを確認する。
- エリアと駅ツリー、無効データの除外を確認する。
- キャッシュミス・ヒット・Redis障害時のDBフォールバックを確認する。
- 実際のフロントエンドOriginから呼び出せることを確認する。

COUNT SQLの書き換えでパラメータ不整合が発生したため、現在の物件ページ検索では`page.setOptimizeCountSql(false)`を使用しています。総件数取得やページネーション自体を無効化する設定ではありません。

JSON応答中の`Broken pipe`はクライアント切断・キャンセルの場合があります。Jacksonのフィールド名が表示されても、そのフィールドの値が不正とは限りません。ブラウザの最終リクエスト結果を確認してください。

### 今後の開発

1. Spring Securityによる登録・ログイン・パスワードハッシュ化・ログイン復元。
2. 用意済みRBACテーブルを用いた認可。
3. ログイン必須の担当者電話番号取得。
4. お気に入り・閲覧履歴・プロフィールの永続化。
5. Cloudflare R2への検証付きファイルアップロード。
6. 地図表示範囲に応じた物件検索。
7. 物件登録・編集・審査・公開管理。
8. メッセージ、添付画像、未読件数、会話へのアクセス制御。
9. 主要業務ルールと連携部分の自動テストを追加。

---

## Project notes / 補足

- Prepared from the current development implementation and shared configuration; no repository-wide build or audit is claimed by this document.
- No license is declared here. Add a repository license if reuse permissions are intended.
- 本書は開発中の実装・共有済み設定をもとにしています。リポジトリ全体のビルド・監査済みを示すものではありません。
- ライセンスは本書では指定していません。再利用を許諾する場合はリポジトリに適切なライセンスを追加してください。

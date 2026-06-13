# SpaceWGBar

Плагин для Paper 1.21+, который показывает бесконечный ActionBar при входе игрока в настроенный регион WorldGuard.

## Возможности

- Поддержка любого количества регионов в `config.yml`
- Adventure API (MiniMessage и legacy `&` коды)
- PlaceholderAPI (опционально)
- Приоритет регионов при пересечении нескольких зон
- Без утечек: сессии очищаются при выходе из региона и отключении игрока

## Требования

| Плагин | Обязательность |
|--------|----------------|
| Paper 1.21+ | Да |
| WorldGuard 7.x | Да |
| PlaceholderAPI | Нет |

## Установка

1. Соберите jar: `mvn clean package`
2. Положите `target/SpaceWGBar-1.0.0.jar` в папку `plugins/`
3. Убедитесь, что WorldGuard установлен
4. Перезапустите сервер
5. Настройте `plugins/SpaceWGBar/config.yml`

## Конфигурация

```yaml
settings:
  update-interval-ticks: 20

regions:
  spawn:
    message: "<green>Welcome, <white>%player_name%<green>!"
    priority: 10
  shop:
    message: "<gold>Shop area"
    priority: 5
```

- **regions** — ключ = ID региона WorldGuard (регистр не важен)
- **message** — текст ActionBar (MiniMessage или `&` коды)
- **priority** — при нахождении в нескольких регионах показывается сообщение с наибольшим приоритетом

Сообщения плагина настраиваются в `lang.yml`.

## Команды

| Команда | Описание | Право |
|---------|----------|-------|
| `/spacewgbar reload` | Перезагрузить конфиг | `spacewgbar.reload` |

Алиасы: `/swgb`, `/wgbar`

## Сборка

```bash
mvn clean package
```

Требуется Java 21.

## Как это работает

WorldGuard не предоставляет события входа/выхода из региона. Плагин отслеживает смену блока (`PlayerMoveEvent`), телепорты и вход на сервер, запрашивая регионы через WorldGuard RegionQuery. ActionBar обновляется по таймеру, пока игрок находится в регионе.

## Лицензия

См. репозиторий проекта.

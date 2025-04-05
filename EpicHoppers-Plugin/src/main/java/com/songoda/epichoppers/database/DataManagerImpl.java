package com.songoda.epichoppers.database;

import com.songoda.core.database.DatabaseConnector;

public class DataManagerImpl {
//    public DataManagerImpl(DatabaseConnector databaseConnector, Plugin plugin) {
//        super(databaseConnector, plugin);
//    }
//
//    @Override
//    public void createBoost(BoostDataImpl boostData) {
//        this.runAsync(() -> {
//            try (Connection connection = this.databaseConnector.getConnection()) {
//                String createBoostedPlayer = "INSERT INTO " + this.getTablePrefix() + "boosted_players (player, multiplier, end_time) VALUES (?, ?, ?)";
//                PreparedStatement statement = connection.prepareStatement(createBoostedPlayer);
//                statement.setString(1, boostData.getPlayer().toString());
//                statement.setInt(2, boostData.getMultiplier());
//                statement.setLong(3, boostData.getEndTime());
//                statement.executeUpdate();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    public void getBoosts(Consumer<List<BoostData>> callback) {
//        List<BoostDataImpl> boosts = new ArrayList<>();
//        this.runAsync(() -> {
//            try (Connection connection = this.databaseConnector.getConnection()) {
//                Statement statement = connection.createStatement();
//                String selectBoostedPlayers = "SELECT * FROM " + this.getTablePrefix() + "boosted_players";
//                ResultSet result = statement.executeQuery(selectBoostedPlayers);
//                while (result.next()) {
//                    UUID player = UUID.fromString(result.getString("player"));
//                    int multiplier = result.getInt("multiplier");
//                    long endTime = result.getLong("end_time");
//                    boosts.add(new BoostDataImpl(multiplier, endTime, player));
//                }
//
//                this.sync(() -> callback.accept(boosts));
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    public void deleteBoost(BoostDataImpl boostData) {
//        this.runAsync(() -> {
//            try (Connection connection = this.databaseConnector.getConnection()) {
//                String deleteBoost = "DELETE FROM " + this.getTablePrefix() + "boosted_players WHERE end_time = ?";
//                PreparedStatement statement = connection.prepareStatement(deleteBoost);
//                statement.setLong(1, boostData.getEndTime());
//                statement.executeUpdate();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    public void deleteLink(HopperImpl hopper, Location location) {
//        this.runAsync(() -> {
//            try (Connection connection = this.databaseConnector.getConnection()) {
//                String deleteLink = "DELETE FROM " + this.getTablePrefix() + "links WHERE hopper_id = ? AND world = ? AND x = ? AND y = ? AND z = ?";
//                PreparedStatement statement = connection.prepareStatement(deleteLink);
//                statement.setInt(1, hopper.getId());
//                statement.setString(2, location.getWorld().getName());
//                statement.setInt(3, location.getBlockX());
//                statement.setInt(4, location.getBlockY());
//                statement.setInt(5, location.getBlockZ());
//                statement.executeUpdate();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    public void deleteLinks(HopperImpl hopper) {
//        this.runAsync(() -> {
//            try (Connection connection = this.databaseConnector.getConnection()) {
//                String deleteHopperLinks = "DELETE FROM " + this.getTablePrefix() + "links WHERE hopper_id = ?";
//                PreparedStatement statement = connection.prepareStatement(deleteHopperLinks);
//                statement.setInt(1, hopper.getId());
//                statement.executeUpdate();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    public void createHoppers(List<HopperImpl> hoppers) {
//        for (HopperImpl hopper : hoppers) {
//            createHopper(hopper);
//        }
//    }
//
//    @Override
//    public void createHopper(HopperImpl hopper) {
//        this.runAsync(() -> {
//            try (Connection connection = this.databaseConnector.getConnection()) {
//                String createHopper = "INSERT INTO " + this.getTablePrefix() + "placed_hoppers (level, placed_by, last_opened_by, teleport_trigger, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
//                try (PreparedStatement statement = connection.prepareStatement(createHopper)) {
//                    statement.setInt(1, hopper.getLevel().getLevel());
//
//                    statement.setString(2,
//                            hopper.getPlacedBy() == null ? null : hopper.getPlacedBy().toString());
//
//                    statement.setString(3,
//                            hopper.getLastPlayerOpened() == null ? null : hopper.getLastPlayerOpened().toString());
//
//                    statement.setString(4, hopper.getTeleportTrigger().name());
//
//                    statement.setString(5, hopper.getWorld().getName());
//                    statement.setInt(6, hopper.getX());
//                    statement.setInt(7, hopper.getY());
//                    statement.setInt(8, hopper.getZ());
//                    statement.executeUpdate();
//                }
//
//                int hopperId = this.lastInsertedId(connection, "placed_hoppers");
//                hopper.setId(hopperId);
//
//                Map<ItemStack, ItemType> items = new HashMap<>();
//                Filter filter = hopper.getFilter();
//
//                for (ItemStack item : filter.getWhiteList()) {
//                    items.put(item, ItemType.WHITELIST);
//                }
//
//                for (ItemStack item : filter.getBlackList()) {
//                    items.put(item, ItemType.BLACKLIST);
//                }
//
//                for (ItemStack item : filter.getVoidList()) {
//                    items.put(item, ItemType.VOID);
//                }
//
//                for (ItemStack item : filter.getAutoSellWhiteList()) {
//                    items.put(item, ItemType.AUTO_SELL_WHITELIST);
//                }
//
//                for (ItemStack item : filter.getAutoSellBlackList()) {
//                    items.put(item, ItemType.AUTO_SELL_BLACKLIST);
//                }
//
//                String createItem = "INSERT INTO " + this.getTablePrefix() + "items (hopper_id, item_type, item) VALUES (?, ?, ?)";
//                try (PreparedStatement statement = connection.prepareStatement(createItem)) {
//                    for (Map.Entry<ItemStack, ItemType> entry : items.entrySet()) {
//                        statement.setInt(1, hopper.getId());
//                        statement.setString(2, entry.getValue().name());
//
//                        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); BukkitObjectOutputStream bukkitStream = new BukkitObjectOutputStream(stream)) {
//                            bukkitStream.writeObject(entry.getKey());
//                            statement.setString(3, Base64.getEncoder().encodeToString(stream.toByteArray()));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            continue;
//                        }
//                        statement.addBatch();
//                    }
//                    statement.executeBatch();
//                }
//
//                Map<Location, LinkType> links = new HashMap<>();
//
//                for (Location location : hopper.getLinkedBlocks()) {
//                    links.put(location, LinkType.REGULAR);
//                }
//
//                if (filter.getEndPoint() != null) {
//                    links.put(filter.getEndPoint(), LinkType.REJECT);
//                }
//
//                String createLink = "INSERT INTO " + this.getTablePrefix() + "links (hopper_id, link_type, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?)";
//                try (PreparedStatement statement = connection.prepareStatement(createLink)) {
//                    for (Map.Entry<Location, LinkType> entry : links.entrySet()) {
//                        statement.setInt(1, hopper.getId());
//
//                        statement.setString(2, entry.getValue().name());
//
//                        Location location = entry.getKey();
//                        statement.setString(3, location.getWorld().getName());
//                        statement.setInt(4, location.getBlockX());
//                        statement.setInt(5, location.getBlockY());
//                        statement.setInt(6, location.getBlockZ());
//                        statement.addBatch();
//                    }
//                    statement.executeBatch();
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    public void updateHopper(HopperImpl hopper) {
//        this.runAsync(() -> {
//            try (Connection connection = this.databaseConnector.getConnection()) {
//                String updateHopper = "UPDATE " + this.getTablePrefix() + "placed_hoppers SET level = ?, placed_by = ?, last_opened_by = ?, teleport_trigger = ? WHERE id = ?";
//                PreparedStatement statement = connection.prepareStatement(updateHopper);
//                statement.setInt(1, hopper.getLevel().getLevel());
//                statement.setString(2, hopper.getPlacedBy().toString());
//                statement.setString(3, hopper.getLastPlayerOpened().toString());
//                statement.setString(4, hopper.getTeleportTrigger().name());
//                statement.setInt(5, hopper.getId());
//                statement.executeUpdate();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    public void deleteHopper(HopperImpl hopper) {
//        this.runAsync(() -> {
//            try (Connection connection = this.databaseConnector.getConnection()) {
//                String deleteHopper = "DELETE FROM " + this.getTablePrefix() + "placed_hoppers WHERE id = ?";
//                try (PreparedStatement statement = connection.prepareStatement(deleteHopper)) {
//                    statement.setInt(1, hopper.getId());
//                    statement.executeUpdate();
//                }
//
//                String deleteHopperLinks = "DELETE FROM " + this.getTablePrefix() + "links WHERE hopper_id = ?";
//                try (PreparedStatement statement = connection.prepareStatement(deleteHopperLinks)) {
//                    statement.setInt(1, hopper.getId());
//                    statement.executeUpdate();
//                }
//
//                String deleteItems = "DELETE FROM " + this.getTablePrefix() + "items WHERE hopper_id = ?";
//                try (PreparedStatement statement = connection.prepareStatement(deleteItems)) {
//                    statement.setInt(1, hopper.getId());
//                    statement.executeUpdate();
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    public void getHoppers(Consumer<Map<Integer, HopperImpl>> callback) {
//        this.runAsync(() -> {
//            try (Connection connection = this.databaseConnector.getConnection()) {
//                Map<Integer, HopperImpl> hoppers = new HashMap<>();
//
//                try (Statement statement = connection.createStatement()) {
//                    String selectHoppers = "SELECT * FROM " + this.getTablePrefix() + "placed_hoppers";
//                    ResultSet result = statement.executeQuery(selectHoppers);
//                    while (result.next()) {
//                        World world = Bukkit.getWorld(result.getString("world"));
//
//                        if (world == null) {
//                            continue;
//                        }
//
//                        int id = result.getInt("id");
//                        int level = result.getInt("level");
//
//                        String placedByStr = result.getString("placed_by");
//                        UUID placedBy = placedByStr == null ? null : UUID.fromString(result.getString("placed_by"));
//
//                        String lastOpenedByStr = result.getString("last_opened_by");
//                        UUID lastOpenedBy = lastOpenedByStr == null ? null : UUID.fromString(result.getString("last_opened_by"));
//
//                        TeleportTrigger teleportTrigger = TeleportTrigger.valueOf(result.getString("teleport_trigger"));
//
//                        int x = result.getInt("x");
//                        int y = result.getInt("y");
//                        int z = result.getInt("z");
//                        Location location = new Location(world, x, y, z);
//
//                        HopperImpl hopper = new HopperBuilder(location)
//                                .setId(id)
//                                .setLevel(((EpicHoppers) this.plugin).getLevelManager().getLevel(level))
//                                .setPlacedBy(placedBy)
//                                .setLastPlayerOpened(lastOpenedBy)
//                                .setTeleportTrigger(teleportTrigger)
//                                .build();
//
//                        hoppers.put(id, hopper);
//                    }
//                }
//
//                try (Statement statement = connection.createStatement()) {
//                    String selectLinks = "SELECT * FROM " + this.getTablePrefix() + "links";
//                    ResultSet result = statement.executeQuery(selectLinks);
//                    while (result.next()) {
//                        World world = Bukkit.getWorld(result.getString("world"));
//
//                        if (world == null) {
//                            continue;
//                        }
//
//                        int id = result.getInt("hopper_id");
//                        LinkType type = LinkType.valueOf(result.getString("link_type"));
//
//                        int x = result.getInt("x");
//                        int y = result.getInt("y");
//                        int z = result.getInt("z");
//                        Location location = new Location(world, x, y, z);
//
//                        HopperImpl hopper = hoppers.get(id);
//                        if (hopper == null) {
//                            break;
//                        }
//
//                        hopper.addLinkedBlock(location, type);
//                    }
//                }
//
//                try (Statement statement = connection.createStatement()) {
//                    String selectItems = "SELECT * FROM " + this.getTablePrefix() + "items";
//                    ResultSet result = statement.executeQuery(selectItems);
//                    while (result.next()) {
//                        int id = result.getInt("hopper_id");
//                        ItemType type = ItemType.valueOf(result.getString("item_type"));
//
//                        ItemStack item = null;
//                        try (BukkitObjectInputStream stream = new BukkitObjectInputStream(
//                                new ByteArrayInputStream(Base64.getDecoder().decode(result.getString("item"))))) {
//                            item = (ItemStack) stream.readObject();
//                        } catch (IOException | ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }
//
//                        HopperImpl hopper = hoppers.get(id);
//                        if (hopper == null) {
//                            break;
//                        }
//
//                        if (item != null) {
//                            hopper.getFilter().addItem(item, type);
//                        }
//                    }
//                }
//                this.sync(() -> callback.accept(hoppers));
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//    }
}

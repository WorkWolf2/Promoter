# 🎮 Promoter - Minecraft TikTok Campaign Plugin

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21-green.svg)](https://www.minecraft.net/)
[![Paper](https://img.shields.io/badge/Paper-API-blue.svg)](https://papermc.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Promoter** is a Minecraft plugin for Paper/Spigot servers that manages TikTok promotional campaigns. The plugin allows administrators to create viral campaigns where players must share and promote TikTok content to receive in-game rewards.

## ✨ Features

- 🎯 **Viral Campaign System**: Create promotional campaigns that automatically spread among players
- 🔗 **TikTok Link Verification**: Automatically check the validity of shared TikTok links
- 🎁 **Reward System**: Automatically distribute rewards to players who complete campaigns
- 📊 **Data Management**: Save and manage campaign data and participants
- 🎨 **User Interface**: Colored and interactive messages with clickable links
- 🔒 **Permission System**: Granular control of features for administrators

## 🚀 Installation

### Prerequisites

- **Java 21** or higher
- **Paper/Spigot 1.21** or higher
- **Administrator permissions** to configure campaigns

### Installation Steps

1. **Download the Plugin**
   ```bash
   # Clone the repository
   git clone https://github.com/yourusername/promoter.git
   cd promoter
   
   # Build the project
   ./gradlew build
   ```

2. **Install on Server**
   - Copy the file `build/libs/Promoter-1.0.jar` to your server's `plugins/` folder
   - Restart the server or reload plugins

3. **Initial Configuration**
   - The plugin will automatically create configuration files
   - Modify `plugins/Promoter/config.yml` to customize rewards

## 📖 Usage

### Available Commands

#### For Administrators

- `/adminlink <link>` - Start a new campaign with the specified TikTok link
  - **Permission**: `promoter.admin`
  - **Description**: Sends the link to 5% of online players randomly

#### For Players

- `/premiotiktok <link>` - Redeem the reward with a new TikTok link
  - **Permission**: None (all players)
  - **Description**: Verifies the link and assigns the reward if valid

### How It Works

1. **Campaign Start**: An administrator uses `/adminlink <link>` to start a campaign
2. **Distribution**: The plugin randomly selects 5% of online players
3. **Promotion**: Selected players receive a message with a clickable link
4. **Sharing**: Players open the link, copy a new TikTok link
5. **Redemption**: Players use `/premiotiktok <link>` to redeem the reward
6. **Verification**: The plugin verifies that the link is different from the received one and matches the video
7. **Reward**: If everything is correct, the player receives the configured reward

## ⚙️ Configuration

### Configuration Files

The plugin uses two main configuration files:

#### `config.yml`
```yaml
# Command executed when a player redeems the reward
# {player} is replaced with the player's name
rewardCommand: "ecrates key give {player} common 1"
```

#### `data.yml`
Automatically generated file that contains:
- Active campaign data
- List of players who have received links
- List of players who have redeemed rewards
- History of shared links

### Customizing Rewards

Modify the reward command in `config.yml`:

```yaml
# Reward command examples
rewardCommand: "give {player} diamond 10"                    # Diamonds
rewardCommand: "eco give {player} 1000"                      # Money
rewardCommand: "lp user {player} permission set vip true"    # VIP permissions
rewardCommand: "kit vip {player}"                            # Custom kit
```

## 🔧 Development

### Project Structure

```
src/main/java/com/minegolem/promoter/
├── Promoter.java              # Main plugin class
├── commands/                  # Plugin commands
│   ├── AdminLinkCommand.java  # Administrator command
│   └── RewardCommand.java     # Player command
├── managers/                  # Plugin managers
│   └── CampaignManager.java   # Campaign management
├── data/                      # Data management
│   └── manager/               # Data file managers
├── scraper/                   # Scraping utilities
│   └── TikTokChecker.java     # TikTok link verification
└── utils/                     # Various utilities
    └── PlayerUtils.java       # Player utilities
```

### Technologies Used

- **Java 21** - Programming language
- **Paper API** - Minecraft plugin API
- **Gradle** - Build system
- **Lombok** - Boilerplate code reduction
- **Adventure API** - Text component management
- **JetBrains Annotations** - Development annotations

### Compilation

```bash
# Clone the repository
git clone https://github.com/yourusername/promoter.git
cd promoter

# Build the project
./gradlew build

# The JAR will be available in build/libs/
```

### Local Testing

```bash
# Start a test server with the plugin
./gradlew runServer
```

## 🛡️ Security

- **Link Verification**: The plugin verifies that TikTok links are valid
- **Duplicate Control**: Prevents the use of duplicate links
- **Permissions**: Permission system to control access to functions
- **Validation**: Checks that links correspond to the original video

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Contribution Guidelines

- Follow Java naming conventions
- Add comments for complex code
- Test your changes before submitting
- Update documentation if necessary

## 📝 License

This project is distributed under the MIT license. See the `LICENSE` file for more details.

## 👨‍💻 Author

**WorkWolf_2** - [GitHub](https://github.com/WorkWolf_2)

## 🙏 Acknowledgments

- **PaperMC** for the Minecraft API
- **Lombok** for boilerplate code reduction
- **Adventure API** for text component management
- All contributors and plugin testers

## 📞 Support

If you have problems or questions:

- 📧 **Email**: [your-email@example.com]
- 💬 **Discord**: [Discord server link]
- 🐛 **Issues**: [GitHub Issues](https://github.com/yourusername/promoter/issues)
- 📖 **Wiki**: [Complete documentation](https://github.com/yourusername/promoter/wiki)

---

⭐ **If this plugin is useful to you, consider giving a star to the repository!**

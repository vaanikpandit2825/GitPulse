<div align="center">

<img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
<img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
<img src="https://img.shields.io/badge/API-GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/>
<img src="https://img.shields.io/badge/Status-Active-00C853?style=for-the-badge"/>

<br/><br/>

<pre align="center">
  ██████╗ ██╗████████╗██████╗ ██╗   ██╗██╗     ███████╗███████╗
 ██╔════╝ ██║╚══██╔══╝██╔══██╗██║   ██║██║     ██╔════╝██╔════╝
 ██║  ███╗██║   ██║   ██████╔╝██║   ██║██║     ███████╗█████╗
 ██║   ██║██║   ██║   ██╔═══╝ ██║   ██║██║     ╚════██║██╔══╝
 ╚██████╔╝██║   ██║   ██║     ╚██████╔╝███████╗███████║███████╗
  ╚═════╝ ╚═╝   ╚═╝   ╚═╝      ╚═════╝ ╚══════╝╚══════╝╚══════╝
</pre>

### **Your GitHub. Analyzed. Ranked. Dueled.**

*Turn your commit history into a developer identity.*

<br/>


</div>

---

## 🧭 What is GitPulse?

**GitPulse** is an Android app that transforms any public GitHub profile into a rich developer dashboard. Enter a username, and in seconds you get a **Dev Score**, contribution heatmap, streak analytics, repo browser, head-to-head **Developer Duels**, **Market Value estimation**, and a beautiful shareable card — all powered by the GitHub API.

Whether you're flexing your consistency, benchmarking yourself against peers, or estimating what your GitHub profile is worth on the job market — GitPulse has you covered.

---

## ✨ Features

### 🧑‍💻 Developer Profile Dashboard
Analyze any public GitHub profile instantly. GitPulse computes a proprietary **Dev Score** based on repositories, commit activity, streaks, and language usage. Profiles are assigned a global tier — from **Bronze** all the way up — giving you a quick sense of where you stand.

> *Followers · Following · Repositories · Top Language · Global Rank · Dev Score*

---

### 📊 Contribution Graph & Streaks
A full-year GitHub-style contribution heatmap with daily granularity. Track your **current streak**, **longest streak**, and **total commits**. The **Streak Guardian** feature monitors how much time you have left in the day to keep your streak alive.

> *"12h 30m left today — Start your streak by committing today!"*

---

### 📁 Repository Browser
Browse all public repositories of any developer with a searchable, filterable list. Each card surfaces the primary language, star count, and last updated timestamp — perfect for quick profile reconnaissance.

---

### ⚔️ Developer Duel
Challenge any GitHub developer to a head-to-head **Duel**. GitPulse generates a side-by-side **Comparison Grid** across six dimensions:

| Metric | You | Opponent |
|---|---|---|
| Followers | — | — |
| Following | — | — |
| Repositories | — | — |
| Longest Streak | — | — |
| Top Language | — | — |
| Dev Score | — | — |

A winner is declared and you can share the result directly.

---

### 💰 Market Value Estimator
GitPulse estimates your **developer market value** based on your top language, commit streak, and repository count — cross-referenced against real-time job market data from the **Adzuna API**. Results are country-specific (supports India and more).

> *Disclaimer: This is a reference estimate. Actual salary depends on experience, education, and negotiation. Always do your own research.*

---

### 🎨 Shareable Dev Card
Generate a beautiful, personalized **Dev Card** and share it anywhere. Choose from four aesthetic themes:

| Theme | Vibe |
|---|---|
| 🌈 Gradient | Vibrant & bold |
| 🌸 Sakura | Soft & elegant |
| 💻 Terminal | Hacker aesthetic |
| 🌌 Cosmic | Dark & galaxy-toned |

---

## 📱 App Preview

<div align="center">

| Home | Profile | Contributions |
|---|---|---|
| *Enter any GitHub username* | *Dev Score + Global Rank* | *Heatmap + Streaks* |

| Repositories | Developer Duel | Market Value |
|---|---|---|
| *Searchable repo list* | *Head-to-head comparison* | *Salary estimate by country* |

</div>

---

## 🏗️ Architecture

GitPulse follows a clean **MVVM** architecture with a unidirectional data flow:

```
┌─────────────────────────────────────────────────────────┐
│                        UI Layer                         │
│         Activities / Fragments / Composables            │
└────────────────────────┬────────────────────────────────┘
                         │ observes
┌────────────────────────▼────────────────────────────────┐
│                    ViewModel Layer                      │
│         State management + Business logic               │
└────────────────────────┬────────────────────────────────┘
                         │ calls
┌────────────────────────▼────────────────────────────────┐
│                   Repository Layer                      │
│              Single source of truth                     │
└────────┬───────────────────────────────┬────────────────┘
         │                               │
┌────────▼────────┐             ┌────────▼────────┐
│   GitHub API    │             │   Adzuna API    │
│  (Public Data)  │             │  (Job Market)   │
└─────────────────┘             └─────────────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Platform | Android (API 24+) |
| Networking | Retrofit + OkHttp |
| Async | Kotlin Coroutines |
| Image Loading | Coil |
| Architecture | MVVM + LiveData |
| GitHub Data | GitHub REST API v3 |
| Salary Data | Adzuna Jobs API |

---

## 🚀 Installation

### Prerequisites
- Android Studio **Hedgehog** or later
- Android device / emulator running API **24+**
- A free [GitHub Personal Access Token](https://github.com/settings/tokens) *(optional — increases rate limits)*
- A free [Adzuna API Key](https://developer.adzuna.com/) *(required for Market Value feature)*

### Setup

```bash
# 1. Clone the repository
git clone https://github.com/vaanikpandit2825/GitPulse.git
cd GitPulse

# 2. Open in Android Studio
# File → Open → select the GitPulse directory

# 3. Add your API keys to local.properties
echo "GITHUB_TOKEN=your_github_token_here" >> local.properties
echo "ADZUNA_APP_ID=your_adzuna_app_id" >> local.properties
echo "ADZUNA_APP_KEY=your_adzuna_app_key" >> local.properties

# 4. Build & Run
# Click ▶ Run in Android Studio, or:
./gradlew assembleDebug
```

### Download APK
> ⬇️ Grab the latest release from [Releases](../../releases/latest)

---

## 🔑 API Keys

| API | Purpose | Free Tier |
|---|---|---|
| [GitHub REST API](https://docs.github.com/en/rest) | Profile, repos, contributions | 60 req/hr (unauthenticated), 5000/hr (token) |
| [Adzuna API](https://developer.adzuna.com/) | Developer salary data | 1000 req/day |

> **Note:** The app works without API keys, but GitHub's unauthenticated rate limit (60 req/hr) may cause temporary failures under heavy use.

---

## 📂 Project Structure

```
GitPulse/
├── app/
│   └── src/main/
│       ├── java/com/gitpulse/
│       │   ├── ui/
│       │   │   ├── home/           # Username entry screen
│       │   │   ├── profile/        # Dev Score + profile overview
│       │   │   ├── contributions/  # Heatmap + streak tracking
│       │   │   ├── repositories/   # Repo browser
│       │   │   ├── duel/           # Developer Duel
│       │   │   ├── marketvalue/    # Salary estimator
│       │   │   └── sharecard/      # Themed dev card
│       │   ├── data/
│       │   │   ├── api/            # Retrofit service interfaces
│       │   │   ├── model/          # Data classes
│       │   │   └── repository/     # Data access layer
│       │   └── utils/              # Score calculator, formatters
│       └── res/
│           ├── layout/             # XML layouts
│           ├── drawable/           # Icons + assets
│           └── values/             # Themes, strings, colors
└── README.md
```

---

## 🗺️ Roadmap

- [x] GitHub profile analysis & Dev Score
- [x] Contribution heatmap + streak tracking
- [x] Repository browser with search
- [x] Developer Duel head-to-head comparison
- [x] Market Value estimation (Adzuna API)
- [x] Shareable Dev Card with multiple themes
- [ ] Offline caching with Room DB
- [ ] GitHub OAuth login for private stats
- [ ] Notification for streak reminders
- [ ] Language trend charts (last 12 months)
- [ ] Widget support for home screen streaks
- [ ] Leaderboard — compare with friends

---

## 🤝 Contributing

Contributions are welcome and appreciated. Here's how to get started:

```bash
# Fork the repo, then:
git checkout -b feature/your-feature-name
git commit -m "feat: describe your change"
git push origin feature/your-feature-name
# Open a Pull Request 🚀
```

Please follow the existing code style and include a brief description in your PR. For major changes, open an issue first to discuss what you'd like to change.

---

## 🐛 Known Issues

- Market Value screen requires a valid Adzuna API key — a connection error is shown otherwise
- GitHub contribution data is based on public activity only
- Rate limiting may apply on unauthenticated requests

---

## 📜 License

```
MIT License

Copyright (c) 2026 GitPulse

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software...
```

See [LICENSE](./LICENSE) for the full text.

---

## 🙏 Acknowledgements

- [GitHub REST API](https://docs.github.com/en/rest) — the backbone of all profile data
- [Adzuna Jobs API](https://developer.adzuna.com/) — real-world salary benchmarking
- [Shields.io](https://shields.io) — beautiful README badges
- Every developer who ever pushed a commit at 11:59 PM to keep their streak alive 🔥

---

<div align="center">

**Built with 💙 in Kotlin**

*If GitPulse helped you flex your GitHub game, drop a ⭐ on the repo!*

</div>

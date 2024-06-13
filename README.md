<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]



<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/Ardnys/cfs-web">
    <img src="https://github.com/Ardnys/cfs-web/blob/main/images/kep.webp" alt="CFS Logo" width="80" height="80">
  </a>

<h3 align="center">Course Feedback System - Web</h3>

  <p align="center">
CFS aims to streamline the crucial feedback loop between teachers and students thus allowing teachers to get feedback from their students more frequently.
    <br />
    <a href="https://github.com/Ardnys/cfs-web"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/Ardnys/cfs-web">View Demo</a>
    ·
    <a href="https://github.com/Ardnys/cfs-web/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    ·
    <a href="https://github.com/Ardnys/cfs-web/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

Course Feedback System (CFS) consists of 3 parts:
- [Web application and backend](https://github.com/Ardnys/cfs-web) for student feedback
- [Mobile application](https://github.com/ThePotatoDuke/CFS) for teachers to request and view feedback
- [Samurai](https://github.com/Ardnys/samurai-rs) - the feedback summary server
<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

* [![Kotlin][Kotlin.org]][Kotlin-url]
* [![Ktor][Ktor.io]][Ktor-url]
* [![Supabase][Supabase.com]][Supabase-url]
* [![Freemarker][Freemarker.org]][Freemarker-url]
* [![Rust][Rust.org]][Rust-url]
* [![HuggingFace][Hugging.co]][Hugging-url]
* [![ChatGPT][ChatGPT]][ChatGPT-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started
The following are needed to run the application.
- Python
- JDK 17 
- Supabase URL and KEY (tables too)
- Gmail Credentials (Theoretically this should be university email API, but we used Gmail as a proof of concept)
- Optional: A custom [Samurai](https://github.com/Ardnys/samurai-rs). See what makes a "Samurai" for this project.

### Prerequisites

Make sure python and java are in PATH and credentials above are set correctly.

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/Ardnys/cfs-web.git
   ```
2. run `run` script
   ```sh
   ./run.sh
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

Feedback form for students. A link to this page is sent to students' email.

  <p align="center">
    <img src="https://github.com/Ardnys/cfs-web/blob/main/images/feedback_form.png" alt="Feedback form for students">
  </p>

Submitted feedback form displays a warm thank you message.

  <p align="center">
    <img src="https://github.com/Ardnys/cfs-web/blob/main/images/feedback_thankyou.png" alt="thank you for your feedback">
  </p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Drop an issue and we'll respond!

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/Ardnys/cfs-web.svg?style=for-the-badge
[contributors-url]: https://github.com/Ardnys/cfs-web/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/Ardnys/cfs-web.svg?style=for-the-badge
[forks-url]: https://github.com/Ardnys/cfs-web/network/members
[stars-shield]: https://img.shields.io/github/stars/Ardnys/cfs-web.svg?style=for-the-badge
[stars-url]: https://github.com/Ardnys/cfs-web/stargazers
[issues-shield]: https://img.shields.io/github/issues/Ardnys/cfs-web.svg?style=for-the-badge
[issues-url]: https://github.com/Ardnys/cfs-web/issues
[license-shield]: https://img.shields.io/github/license/Ardnys/cfs-web.svg?style=for-the-badge
[license-url]: https://github.com/Ardnys/cfs-web/blob/master/LICENSE.txt
[product-screenshot]: images/screenshot.png
[Rust.org]: https://img.shields.io/badge/Rust-000000?style=for-the-badge&logo=rust&logoColor=black&labelColor=white
[Rust-url]: https://www.rust-lang.org/
[ChatGPT]: https://img.shields.io/badge/GPT-412991?style=for-the-badge&logo=openai&logoColor=white&labelColor=black
[ChatGPT-url]: https://vuejs.org/
[Hugging.co]: https://img.shields.io/badge/HuggingFace-FFD21E?style=for-the-badge&logo=huggingface&logoColor=white&labelColor=black
[Hugging-url]: https://huggingface.co/facebook/bart-large-cnn
[Freemarker.org]: https://img.shields.io/badge/Freemarker-326CAC?style=for-the-badge&logo=apachefreemarker&logoColor=white&labelColor=black
[Freemarker-url]: https://freemarker.apache.org/index.html
[Supabase.com]: https://img.shields.io/badge/Supabase-3FCF8E?style=for-the-badge&logo=supabase&logoColor=3FCF8E&labelColor=black
[Supabase-url]: https://laravel.com
[Ktor.io]: https://img.shields.io/badge/Ktor-563D7C?style=for-the-badge&logo=ktor&logoColor=white&labelColor=black
[Ktor-url]: https://ktor.io/
[Kotlin.org]: https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=7F52FF&labelColor=black
[Kotlin-url]: https://kotlinlang.org/

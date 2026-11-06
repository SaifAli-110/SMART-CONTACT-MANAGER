const mobileMenu = document.getElementById('mobile-menu');
const navMenu = document.querySelector('.nav-menu');

mobileMenu.addEventListener('click', () => {
    mobileMenu.classList.toggle('active');
    navMenu.classList.toggle('active');
});

// Close menu when a link is clicked (Mobile)
document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', () => {
        mobileMenu.classList.remove('active');
        navMenu.classList.remove('active');
    });
});

const toggleSideBar = () => {
    if($(".sidebar").is(":visible")) {
    //     true block
    //     if the sidebar is visible on the screen do close the sidebar
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");

    }else{
    //     false block
    // if the sidebar is not visible on the screen do open the sidebar
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
    }
}
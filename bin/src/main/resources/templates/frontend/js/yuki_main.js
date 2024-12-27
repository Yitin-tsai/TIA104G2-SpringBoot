// Region selection changes available cities
document.getElementById('region').addEventListener('change', function () {
  const citySelect = document.getElementById('city');
  const region = this.value;

  citySelect.innerHTML = '<option value="">地區</option>';

  if (region === 'kanto') {
    addCityOption('tokyo', '東京');
    addCityOption('yokohama', '横浜');
  } else if (region === 'kansai') {
    addCityOption('kyoto', '京都');
    addCityOption('osaka', '大阪');
  } else if (region === 'kyushu') {
    addCityOption('fukuoka', '福岡');
    addCityOption('nagasaki', '長崎');
  }
});

function addCityOption(value, text) {
  const citySelect = document.getElementById('city');
  const option = document.createElement('option');
  option.value = value;
  option.textContent = text;
  citySelect.appendChild(option);
}

// Search functionality
document.getElementById('priceRange').addEventListener('change', function () {
  const region = document.getElementById('region').value;
  const city = document.getElementById('city').value;
  const priceRange = this.value;

  console.log(`Searching for: Region: ${region}, City: ${city}, Price Range: ${priceRange}`);
  filterTickets(region, city, priceRange);
});

function filterTickets(region, city, priceRange) {
  const tickets = document.querySelectorAll('.ticket-card');

  tickets.forEach(ticket => {
    const price = parseInt(ticket.querySelector('.price').textContent.replace(/[^0-9]/g, ''));
    let show = true;

    if (priceRange) {
      const [min, max] = priceRange.split('-').map(Number);
      if (max) {
        show = price >= min && price <= max;
      } else {
        // For "5000+" option
        show = price >= min;
      }
    }

    ticket.style.display = show ? 'block' : 'none';
  });
}

// Date search functionality
document.getElementById('dateButton').addEventListener('click', function () {
  const dateInput = document.getElementById('dateInput').value;
  console.log(`Searching for date: ${dateInput}`);
});

document.querySelectorAll('.pagination .page-number').forEach(pageLink => {
  pageLink.addEventListener('click', function (e) {
    e.preventDefault();
    // Remove active class from all page numbers
    document.querySelectorAll('.pagination .page-number').forEach(el => {
      el.classList.remove('active');
    });
    // Add active class to clicked page number
    this.classList.add('active');

    // Here you would typically fetch the data for the selected page
    const pageNumber = this.textContent;
    console.log(`Loading page ${pageNumber}`);
  });
});


document.querySelectorAll('.no-action').forEach(function (link) {
  link.addEventListener('click', function (event) {
    event.preventDefault(); // 阻止商品頁的商品點選會往上跳
  });
});

